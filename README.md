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
	headless=[true, false]
	driver
	timeout=[seconds > 0]
	wait=[milliseconds > 0]
	url
	automator=[enroller, houser]
	username
	password
	term
	
Headless browser mode is currently only supported by Google Chrome and Mozilla Firefox.
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
The houser automatically makes searches for rooms using SPIRE's four-step filter, and if
    any rooms are found, will automatically assign the user to the first room in the results
    list. The houser can iterate over various search configurations for flexibility, which are
    specified using the runtime parameters described below.

**It is important that the search criteria configurations are specific, such that the user would
    be equally satisfied being assigned to any of the rooms that the search might return.** It is
    typically not a good idea to make blanket queries (for example, search for all open single
    rooms), because there may be many results, and the houser will simply assign the user to the
    first room in that list, which is typically in alphabetical order by building name.

The user specifies their search criteria by each radio button and dropdown menu. Some dropdown
    menu criteria are dependent on the setting of a radio button. If a certain setting makes it
     such that a dropdown menu is not visible, that setting does not need to be set.
     It is assumed that all of the user's search configurations are for the same term/semester,
     so that setting is universal to all search configurations. This is not the case with the
     appointment process, as there are points in time where a user may have several active
     assignment appointments, and this setting should be set for each search configuration.
     If the user does not specify some setting(s) in a search configuration, or does not pass
     any runtime search configuration parameters at all, the houser will prompt the user for
     input at runtime, and remember his/her inputs for the lifetime of the process.

The user may make several search configurations by numerically specifying them. If specified,
    each parameter's search configuration number is prepended to and delimited from the
    criteria by a hyphen. If the user intends to only have one search configuration, he/she
    does not need to prepend any numerical criteria configuration at all.
Parameters with a prefix of `[00-]` may be set for a specific room search configuration index.
	For example, to set `s2radio=building` for the third configuration, the argument would be
	`3-s2radio=building`. If the prefix is not included, the parameter will be set for the
	first configuration.
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