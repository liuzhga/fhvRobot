/*
 * Controller.cpp
 *
 *  Created on: 13.11.2015
 *      Author: Nicolaj Hoess
 */

#include "../../include/Controller.h"
#include "../../include/Debugger.h"
#include <unistd.h>

namespace FhvRobot {

Controller::Controller() {
	connection = NULL;
}

Controller::~Controller() {
	delete(connection);
}

void Controller::Init()
{
	robot.MotorStop();

}

void Controller::Start(char* serverIp) {
	connection = new ConnectionAPI(this);
	UdpConnection udp;
	SessionLayer sess(&udp);
	PresentationLayer pres(&sess);
	ApplicationLayer app(&pres);
	udp.SetCallback(&sess);
	sess.SetCallback(&pres);
	pres.SetCallback(&app);
	app.SetCallback(connection);

	connection->SetConnection(&app);
	// Fail tests
	bool res;
	res = connection->Connect("Controlled Nico", serverIp, 998);
	(void) res;

	while(true)
	{
		connection->SendHeartBeat();

		usleep(1000000);
	}
}

void Controller::MotorCommand(unsigned int motorNum, int motorSpeed)
{
	Debugger(VERBOSE) << "Controller got motor command " << motorNum << " with payload=" << motorSpeed << "\n";
	if (motorNum == MOTOR_BOTH)
	{
		robot.MotorLeft(motorSpeed);
		robot.MotorRight(motorSpeed);
	}
	else if (motorNum == MOTOR_RIGHT)
	{
		robot.MotorRight(motorSpeed);
	}
	else if (motorNum == MOTOR_LEFT)
	{
		robot.MotorLeft(motorSpeed);
	}
}

void Controller::CameraEnable(bool cameraEnable)
{

}

} /* namespace FhvRobot */
