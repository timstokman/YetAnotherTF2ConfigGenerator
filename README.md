Yet Another TF2 Config Generator
================================

This program offers advanced configuration options for team fortress 2. It uses the tf2 scripting language. This won't get you VAC banned.

Features
--------

* Ability to specify for each class/weapon to specify the:
  - Crosshair (color, type, scale)
  - Mouse Sensitivity
  - Showing/Hiding behaviour of the weapon when you shoot/move/switch weapons/reload/etc..
  - Dingalings (damage sounds)
  - Interp-ratio
  - Batch damage numbers
* Weapon switching has integrated next weapon, previous weapon, last weapon, toggle primary/secondary, toggle current/melee binds
* Null-movement canceling: if you press two opposite strafe keys, you will no longer stand still
* Ability to zoom in (up to factor 1.2)
* Crouch jump (default off, can be enabled on a by-class basis)
* Spawn switch binds
* Engineer: quickbuild binds
* Spy: disguise cycle
* Medic: radar, communicate through chat when ubering, fake charge
* Hide/Show gibs/sprays/ragdolls
* Chris' configs can be optionally included
* Scoreboard can be integrated with the network graphs
* Integrate your own scripts easily from within the program itself
* Binds for reload the hud, fixing graphical glitches and suicide
* Team only voice option
* Option to hide hitscan tracers/flames from the flamethrower/healbeam
* Also includes all the advanced options tf2 has 

Screenshots
-------------

![Main options screen](http://i.imgur.com/v1RUU)

![Spy binds](http://i.imgur.com/TF4LT)

![Medic options](http://i.imgur.com/z2UU5)

![Weapon and crosshair configuration](http://i.imgur.com/jkeMp)

Installation
-------------

* Download the program [here](https://github.com/logophobia/YetAnotherTF2ConfigGenerator-Distribution/archive/master.zip)
* Unzip the program and double click on the jar file to start (make sure you have a recent version of java installed)
* Set everything up the way you want to, make sure you copy most of your essential settings in tf2, like your mouse sensitivity (very annoying when that gets changed). Use the profile menu to load some default settings
* Select your steam directory and username and save the configuration files to tf2 

Uninstallation
---------------

* Delete all the .cfg files inside the tf/cfg directory
* Run tf2 once with the -autoconfig flag (inside the launch options) to reset tf2 to default
* Place any backed-up cfg files back

FAQ
----

### I am having issues, it crashes. How can I fix this?

> Try the following things:
> * Make sure you unzip your program before running it, otherwise it won't be able to find any of the files it needs
> * Upgrade java to its latest version, earlier java version don't work very well with this program
> * Try to generate the scripts locally, and copying them manually the the tf/cfg directory in the team fortress installation directory
> * Post an explanation + screenshot (if applicable) with the crash/issue on my github page: github.com/logophobia/YetAnotherTF2ConfigGenerator or add me at steamcommunity.com/id/logophobia

### What is viewmodel switch mode?

> It indicates when the viewmodel is turned off if that is set for the weapon slot:
> * Immediately after switching weapons
> * After firing a weapon
> * After switching strafe directions
> * After either firing or strafing

### I have a feature request, where can I post it?

> Post any suggestions on the github page or in the thread where you found the program

### What does medic radar do precisely?

> It toggles medic autocall to 150%, so each of your teammates has a medic symbol above their heads, which you can see through walls. As soon as you let go of the button, it will go back to its original autocall level

### How can I customize this even more?

> You could try editing the original templates, in the template directory. First, delete the cache directory. Then edit the templates, the language used is ssp: http://scalate.fusesource.org/documentation/ssp-reference.html. After that, the program will use your editted version of the templates. 

### What other ways to customize tf2 are there?

> You can install custom huds, skins, hitsounds or customize/make your own scripts

### My ambassador crosshair disappeared, how can I fix this?

> For some reason, the crosshair after shooting is about half the default size. Increasing the size of the spy primary crosshair should fix this.

### Changing settings inside tf2 doesn't work, it just gets changed back. How can I change my settings?

> The scripts that were installed now regulate most of your settings inside tf2. Change stuff through the program, and then reinstall the scripts, or uninstall the scripts and be free to change things inside tf2 again.

### Can I use my settings from a version of your program before 1.0

> Yes. Drag the settings.xml file from your old program into the profile folder of the new program. Now you should be able to load your settings from the profile menu.

