// pid.h

#ifndef _PID_h
#define _PID_h

#if defined(ARDUINO) && ARDUINO >= 100
	#include "Arduino.h"
#else
	#include "WProgram.h"
#endif

class PidClass
{
 protected:
	 float lastErrorValue;

	 float pConstant;
	 float iConstant;
	 float dConstant;

	 float pValue;
	 float iValue;
	 float dValue;

 public:
	PidClass();
	~PidClass();
	
	void setConstants(float p, float i, float d);
	void update(float error, long time);
	void reset();

	float getP();
	float getI();
	float getD();

	float getValue();
};

extern PidClass Pid;

#endif

