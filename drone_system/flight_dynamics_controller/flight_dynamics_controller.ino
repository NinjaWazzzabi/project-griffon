#include <VirtualWire_Config.h>
#include <VirtualWire.h>
#include <Adafruit_BNO055.h>
#include <Adafruit_PWMServoDriver.h>
#include "Vector3f.h"
#include "pid.h"

#define THROTTLE_CH 0
#define RUDDER_CH 1
#define ELEVATOR_CH 2
#define AILERON_CH 3

#define MAX_SERVO_VALUE 2000
#define MIN_SERVO_VALUE 1000
#define SERVO_UPDATE 100

#define ASCII_EOT 4
#define ASCII_SOH 1
#define ASCII_ACK 6
#define UPDATE_DELAY 10

const String desc = "AHRS,CTRL";
const unsigned char descLength = 9;

Vector3fClass actual;
Vector3fClass desired;

float throttleValue;
float rudderValue;
float elevatorValue;
float aileronValue;

PidClass xPid;
PidClass yPid;
PidClass zPid;

Adafruit_PWMServoDriver servoController = Adafruit_PWMServoDriver();
Adafruit_BNO055 ahrs = Adafruit_BNO055(55); //Attitude heading reference system

#define BUFFER_SIZE 16
char serialBuffer[BUFFER_SIZE];
unsigned char bufferIndex;

long lastHostAhrsUpdate;

void setup()
{
	//Serial
	Serial.begin(9600);
	Serial.setTimeout(200);
	lastHostAhrsUpdate = 0;
	bufferIndex = 0;

	// Sends it's desciption until it has gotten it back.
	while (true)
	{
		// Start of heading
		Serial.write(ASCII_SOH);
		Serial.flush();

		// Desciption
		Serial.print(desc);
		Serial.flush();

		// End of transmission
		Serial.write(ASCII_EOT);
		Serial.flush();

		delay(100);
		if (Serial.available() >= descLength)
		{
			if (Serial.readString().equals(desc))
			{
				break;
			}
		}
	}

	//PIDs
	xPid.setConstants(1, 0, 0);
	yPid.setConstants(1, 0, 0);
	zPid.setConstants(1, 0, 0);

	//Servos
	servoController.begin();
	servoController.setPWMFreq(SERVO_UPDATE);

	//Controlvalues
	throttleValue = 0;
	rudderValue = 0;
	elevatorValue = 0;
	aileronValue = 0;

	while (true)
	{
		if (ahrs.begin())
		{
			break;
		}
	}
	ahrs.setExtCrystalUse(true);
}

long time = 0;

void loop()
{
	//Read command data if there's any
	readCommands();

	//Update the actual
	imu::Vector<3> vector = ahrs.getVector(Adafruit_BNO055::VECTOR_EULER);

	// vector.y = -pitch
	// vector.z = roll
	// vector.x = yaw
	actual.setX(-vector.y());
	actual.setY(vector.z());
	actual.setZ(vector.x());

	//Calculate current error
	float xError = desired.getX() - actual.getX();
	float yError = desired.getY() - actual.getY();
	float zError = desired.getZ() - actual.getZ();

	//Calculate next action
	long currentTime = millis();
	xPid.update(xError, currentTime);
	yPid.update(yError, currentTime);
	zPid.update(zError, currentTime);

	//throttleValue = desired.getLength();
	rudderValue = zPid.getValue();
	elevatorValue = xPid.getValue();
	aileronValue = yPid.getValue();

	//Do action to minimize error
	setServoValue(THROTTLE_CH, throttleValue);
	setServoValue(RUDDER_CH, rudderValue);
	setServoValue(ELEVATOR_CH, elevatorValue);
	setServoValue(AILERON_CH, aileronValue);

	if (millis() - lastHostAhrsUpdate > UPDATE_DELAY)
	{
		sendAhrs();
		lastHostAhrsUpdate = millis();
	}

	// Timing Debug Check
	// long deltaTime = millis() - time;
	// time = millis();
	// Serial.println(deltaTime);
}

//Tested!
bool serialError = false;
void readCommands()
{
	while (Serial.available() > 0)
	{
		char input = Serial.read();
		if (input == ASCII_EOT)
		{
			if (!serialError && bufferIndex == 4)
			{
				parseFlightCommand(serialBuffer);
			}
			serialError = false;
			bufferIndex = 0;
		}
		else if (!serialError)
		{
			if (serialError || bufferIndex >= BUFFER_SIZE)
			{
				serialError = true; //Buffer overflow!
			}
			else
			{
				serialBuffer[bufferIndex] = input;
				bufferIndex++;
			}
		}
	}
}

//Tested!
void parseFlightCommand(char *vector)
{
	float throttle = (float)(char)*vector;
	float rudder = (float)(char)*(vector + 1);
	float pitch = (float)(char)*(vector + 2);
	float roll = (float)(char)*(vector + 3);

	desired.setX(pitch);
	desired.setY(roll);
	desired.setZ(rudder);
	throttleValue = throttle * 2 - 100;
}

//Tested!
void sendAhrs()
{
	Serial.print("<");
	printFloat(actual.getX());
	Serial.print(",");
	printFloat(actual.getY());
	Serial.print(",");
	printFloat(actual.getZ());
	Serial.print(">");
	Serial.write(ASCII_EOT);
	Serial.flush();
}

//Tested!
void printFloat(float f)
{
	Serial.print(f, 4);
}

/*
id range: 0 to 15
value range: -100 to 100
*/
//Tested!
void setServoValue(int id, int value)
{

	long mappedValue = map(
		value,
		-100,
		100,
		410,
		820
	);
    servoController.setPin(id, mappedValue);
}