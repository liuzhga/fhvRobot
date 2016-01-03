/*
 * Controller.cpp
 *
 *  Created on: 13.11.2015
 *      Author: Nicolaj Hoess
 */

#include "../../include/Controller.h"
#include "../../include/Debugger.h"
#include <unistd.h>

#define DEFAULT_ROBOT_PORT		(998)

namespace FhvRobot {

Controller::Controller() {
	connection = NULL;
}

Controller::~Controller() {
	delete(connection);
}

void Controller::Init()
{
	robot.MotorStop(true);
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
	bool res = false;

	while (res == false) {
		Debugger(INFO) << "Trying to connecto to the server " << serverIp << " at " << DEFAULT_ROBOT_PORT << "\n";

		res = connection->Connect("Controlled Nico", serverIp, DEFAULT_ROBOT_PORT);
		if (res == false) {
			Debugger(WARNING) << "Connection was not succesful. Trying to reconnect in 10s...\n";
			usleep(10 * 1000 * 1000);
		}
	}
	Debugger(INFO) << "Connection was succesful\n";
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
		robot.MotorLeft(motorSpeed, false);
		robot.MotorRight(motorSpeed, false);
	}
	else if (motorNum == MOTOR_RIGHT)
	{
		robot.MotorRight(motorSpeed, false);
	}
	else if (motorNum == MOTOR_LEFT)
	{
		robot.MotorLeft(motorSpeed, false);
	}
}

void Controller::CameraEnable(bool cameraEnable)
{

}

} /* namespace FhvRobot */
