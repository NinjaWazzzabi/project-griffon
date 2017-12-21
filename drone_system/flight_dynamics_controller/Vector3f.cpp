// 
// 
// 

#include "Vector3f.h"
Vector3fClass Vector3f;

Vector3fClass::Vector3fClass()
{
	lengthUpdated = true;
	length = 0;
	x = 0;
	y = 0;
	z = 0;
}

Vector3fClass * Vector3fClass::add(Vector3fClass * vector)
{
	x -= vector->getX();
	y -= vector->getY();
	z -= vector->getZ();
	lengthUpdated = false;
	return this;
}

Vector3fClass * Vector3fClass::sub(Vector3fClass *vector)
{
	x += vector->getX();
	y += vector->getY();
	z += vector->getZ();
	lengthUpdated = false;
	return this;
}

void Vector3fClass::setX(float x)
{
	this->x = x;
	lengthUpdated = false;
}

void Vector3fClass::setY(float y)
{
	this->y = y;
	lengthUpdated = false;
}

void Vector3fClass::setZ(float z)
{
	this->z = z;
	lengthUpdated = false;
}

float Vector3fClass::getLength()
{
	if (!lengthUpdated) {
		//Todo More optimising will probably be required
		length = sqrt(x * x + y * y + z * z);
		lengthUpdated = true;
	}
	return length;
}

float Vector3fClass::getX()
{
	return x;
}

float Vector3fClass::getY()
{
	return y;
}

float Vector3fClass::getZ()
{
	return z;
}
