// 
// 
// 

#include "pid.h"


PidClass Pid;

PidClass::PidClass()
{
	pConstant = 0;
	iConstant = 0;
	dConstant = 0;

	pValue = 0;
	iValue = 0;
	dValue = 0;
}

PidClass::~PidClass()
{
}

void PidClass::setConstants(float p, float i, float d)
{
	pConstant = p;
	iConstant = i;
	dConstant = d;
}

void PidClass::update(float error, long time)
{
	//Caclulate new P
	pValue = pConstant * error;

	//Calculate new I
	iValue += iConstant * error;

	//Calculate new D
	float deltaError = error - lastErrorValue;
	dValue = dConstant * deltaError;
}

void PidClass::reset()
{
	pValue= 0;
	iValue = 0;
	dValue = 0;
}

float PidClass::getP()
{
	return pValue;
}

float PidClass::getI()
{
	return iValue;
}

float PidClass::getD()
{
	return dValue;
}

float PidClass::getValue()
{
	return pValue + iValue + dValue;
}
