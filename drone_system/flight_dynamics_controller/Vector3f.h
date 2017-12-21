// Vector3f.h

#ifndef _VECTOR3F_h
#define _VECTOR3F_h

#if defined(ARDUINO) && ARDUINO >= 100
	#include "Arduino.h"
#else
	#include "WProgram.h"
#endif

class Vector3fClass
{
 protected:
	 bool lengthUpdated;
	 float length;

	 float x;
	 float y;
	 float z;

 public:
	 Vector3fClass();

	 Vector3fClass* add(Vector3fClass *vector);
	 Vector3fClass* sub(Vector3fClass *vector);

	 void setX(float x);
	 void setY(float y);
	 void setZ(float z);

	 float getLength();

	 float getX();
	 float getY();
	 float getZ();
};

extern Vector3fClass Vector3f;

#endif

