#include <VirtualWire_Config.h>
#include <VirtualWire.h>
#include <Adafruit_BNO055.h>
#include <Adafruit_PWMServoDriver.h>
#include "Vector3f.h"
#include "pid.h"

#define THROTTLE_CH	1
#define RUDDER_CH	2
#define ELEVATOR_CH	3
#define AILERON_CH	4

#define MAX_SERVO_VALUE 2000
#define MIN_SERVO_VALUE 1000

#define ASCII_EOT 4
#define ASCII_ACK 6
#define UPDATE_DELAY 10

const String desc = "AHRS";

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
char serialBuffer[12];

long lastHostAhrsUpdate;

void setup() 
{
	//Serial
	Serial.begin(9600);
	Serial.setTimeout(200);
	lastHostAhrsUpdate = 0;

	// Sends it's desciption until it has gotten it back.
	while (true) {
		Serial.print(desc);
		Serial.flush();
		Serial.write(ASCII_EOT);
		Serial.flush();
		delay(100);
		if (Serial.available() > 3) {
			if (Serial.readString().equals(desc)) {
				break;
			}
		}
	}

	//PIDs
	xPid.setConstants(1, 0, 0);
	yPid.setConstants(1, 0, 0);
	zPid.setConstants(1, 0, 0);

	//Servos
	servoController.setPWMFreq(60);

	//Controlvalues
	throttleValue = 0;
	rudderValue = 0;
	elevatorValue = 0;
	aileronValue = 0;

	while (true) {
		if (ahrs.begin()) {
			break;
		}
	}
	ahrs.setExtCrystalUse(true);
}


long time = 0;

void loop() 
{
	//Read command data if there's any
	if (Serial.available() >= 12)
	{
		for (int i = 0; i < 12; i++) 
		{
			serialBuffer[i] = Serial.read();
		}
		desired.setX(*((float *) &serialBuffer[0]));
		desired.setY(*((float *) &serialBuffer[4]));
		desired.setZ(*((float *) &serialBuffer[8]));
	}

	//Update the actual 
	imu::Vector<3> vector = ahrs.getVector(Adafruit_BNO055::VECTOR_EULER);

	// vec.y : -pitch
	// vec.z : roll
	// vec.x : yaw
	actual.setX(vector.y());
	actual.setY(-vector.z());
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

	throttleValue = desired.getLength();
	rudderValue += zError;
	elevatorValue += yError;
	aileronValue += xError;

	//Do action to minimize error
	setServoValue(THROTTLE_CH, throttleValue);
	setServoValue(RUDDER_CH, rudderValue);
	setServoValue(ELEVATOR_CH, elevatorValue);
	setServoValue(AILERON_CH, aileronValue);

	if (millis() - lastHostAhrsUpdate > UPDATE_DELAY) {
		sendAhrs();
		lastHostAhrsUpdate = millis();
	}

	// Timing Debug Check
	// long deltaTime = millis() - time;
	// time = millis();
	// Serial.println(deltaTime);
}

void sendAhrs() {
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

void printFloat(float f) 
{
	Serial.print(f,4);
}

/*
id range: 0 to 15
value range: -100 to 100
*/
void setServoValue(int id, int value) 
{
	//Todo Test with hardware to verify correct behaviour
	long mappedValue = map(value, -100, 100, MIN_SERVO_VALUE, MAX_SERVO_VALUE);
	servoController.setPWM(id, 0, value);
}