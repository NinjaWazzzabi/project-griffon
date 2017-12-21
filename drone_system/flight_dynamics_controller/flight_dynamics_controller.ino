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

void setup() 
{
	//Serial
	Serial.begin(9600);
	Serial.println("INIT");

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

	Serial.println("TEST");
	while (true) {
		if (ahrs.begin()) {
			break;
		}
		else {
			Serial.println("BNO055 ERROR");
		}
	}

	delay(1000);
	ahrs.setExtCrystalUse(true);
	Serial.println("INIT COMPLT");
}

void loop() 
{
	//Read command data if any
	if (Serial.available() >= 12)
	{
		for (int i = 0; i < 12; i++) 
		{
			serialBuffer[i] = Serial.read();
		}
		desired.setX(*((float *)serialBuffer[0]));
		desired.setY(*((float *)serialBuffer[4]));
		desired.setZ(*((float *)serialBuffer[8]));
	}

	//Update the actual 

	imu::Vector<3> vector = ahrs.getVector(Adafruit_BNO055::VECTOR_EULER);
	//sensors_event_t ahrsEvent;
	//ahrs.getEvent(&ahrsEvent);
	//actual.setX(ahrsEvent.orientation.x);
	//actual.setY(ahrsEvent.orientation.y);
	//actual.setZ(ahrsEvent.orientation.z);

	actual.setX(vector.x());
	actual.setY(vector.y());
	actual.setZ(vector.z());

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

	printFloat(actual.getX());
	printFloat(actual.getY());
	printFloat(actual.getZ());

	Serial.println("---------------------");
	Serial.flush();
}

void printFloat(float f) 
{
	Serial.println(f,4);
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