/*
 * Controller.h
 *
 *  Created on: 13.11.2015
 *      Author: Nicolaj Hoess
 */

#ifndef SOURCE_IMPL_CONTROLLER_H_
#define SOURCE_IMPL_CONTROLLER_H_

#include "ConnectionAPI.h"
#include "Robot.h"

namespace FhvRobot {

class Controller : ApplicationCallback {
private:
	Robot robot;
	ConnectionAPI* connection;
public:
	Controller();
	virtual ~Controller();

	void Init();
	void Start(char* serverIp); // Non-Returning

	void MotorCommand(unsigned int motorNum, int motorSpeed);
	void CameraEnable(bool cameraEnable);
};

} /* namespace FhvRobot */

#endif /* SOURCE_IMPL_CONTROLLER_H_ */
