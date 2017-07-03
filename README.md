# SPIRE Automator
## Introduction
The SPIRE Automator takes runtime arguments to set its functional configurations.
Each section describes an automator and lists its needed runtime arguments.
Some arguments can understand only the values that are listed below.
Arguments listed below that do not list predefined values may take any input.
If an argument with predefined values is given a value that is not listed below,
the program will treat that argument as if it had not been set at all.
If an argument is not set, the automator will prompt the user for it if it is needed.
The automator will not prompt the user for unnecessary arguments.
It is recommended to wrap all parameter/value arguments in quotes to preserve spaces,
especially arguments without predefined values. Here is an example of a good command:

	java -jar spireautomator.jar "browser=chrome" "automator=enroller" "term=Fall 1863"

### Disclaimer
This program is distributed under the MIT license. It is open-source and free to use by anyone.
It may be viewed, modified, and distributed. Use this program at your own risk.
Any damages (such as lost class seats or housing assignments) are the responsibility of
the user and not the author. This tool does not "hack" SPIRE; it is merely an automator.
It does not perform any action that the user would not normally be able to perform manually;
it just performs those actions automatically.

## General
The following are runtime arguments used universally by all automators:

	logging=[off, severe, warning, info, config, all]
	browser=[chrome, firefox, internetexplorer, edge, safari]
	driver
	timeout=[seconds > 0]
	wait=[milliseconds > 0]
	url
	automator=[enroller, houser]
	username
	password
	term
## Enroller
The enroller allows the user to add, drop, swap, and edit classes under customizable conditions.
The program refreshes SPIRE at least every five seconds and continuously checks the conditions
of each action. When all of the conditions of an action are met, the action attempts to perform.
The program concludes when all actions have been successfully performed.

To use this program, the user must manually hardcode conditions and actions, as well as
the classes associated with them, and then build and run the program. Actions and conditions
should be carefully considered to work under any situation. The user should consider potential
class conflicts and outstanding prerequisites. Actions may be created during runtime but will
not have any conditions, and will attempt to perform on every refresh cycle until successful.
There are no runtime arguments needed for the enrollment automator.
An editable example of enroller configurations may be found in:
	`spire.SpireAutomator.setEnrollerConfiguration()`
## Houser
The houser automates the process of searching for and assigning oneself to a room in SPIRE.
The houser is capable of searching for rooms using the same search criteria that the main
	SPIRE website provides, parsing the available rooms, determining whether an available room
	is better than the room currently assigned to the user, and assigning oneself to the room.
The automator will prompt the user for input if a value is needed but not set.
Parameters with a prefix of `[00-]` may be set for a specific room search configuration index.
	For example, to set `s2radio=building` for the third configuration, the argument would be
	`3-s2radio=building`. Prefixes are optional. If the prefix is not included,
	the parameter will be set for the first configuration.
The following arguments are used by this automator:

	searches=[>1]
	forever=[true, false]
	[00-]process
	[00-]s2radio=[building, cluster, area, all]
	[00-]s2select
	[00-]s3radio=[type, design, floor, option]
	[00-]s3select
	[00-]s4radio=[none, room_open, suite_open, type, open_double, open_triple]
    [00-]s4select 