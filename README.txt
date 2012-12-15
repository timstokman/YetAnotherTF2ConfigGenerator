Yet Another TF2 Config Generator
================================

This program generates configuration files for Team Fortress 2

Features
--------

* Ability to specify for each class/weapon-slot to specify the:
  - Crosshair (color, type, scale)
  - Sensitivity
  - Showing/Hiding the weapon model
  - Dingaling (volume, max/min pitch)
  - Interp-ratio
  - Tweakable viewmodel hiding behaviour. After switching weapons, hide the viewmodel after strafing/shooting or immediately
* Null-movement canceling: if you press two opposite strafe keys, you will no longer stand still
* Can include chris' network/graphics config
* Option to hide the tracers, flames or the medic healbeam
* Option to show a netgraph with the scoreboard to identify network issues
* Option to turn gibs/ragdolls/sprays on/off
* Most of the useful advanced options you can already find in the tf2 menu (autoreload, fastswitch, damagenumbers, etc)
* Crouchjump bind, with the option to exclude it for certain classes (default excluded for demoman/soldier)
* Team-only voice bind
* Bind for quick-switching weapon loadouts
* Bind for toggling to a forward spawn (if you want to retain uber or types of charge, switch weapons with the quickswitch bind instead)
* Binds for switching primary/secondary, switching current/melee, previous weapon, next inventory and previous inventory weapon
* Bind for suicide, reloading the hud and fixing graphical glitches
* Binds for all the voice commands
* Medic: Medic radar bind, fake uber bind, option to communicate ubers automatically through chat
* Spy: A disguise cycle bind (separate friendly and enemy disguise cycle) which cycles through a list of predefined disguises. Quickdisguise binds (enemy and friendly). Temporarily shows viewmodel when using cloak/dr
* Engineer: Quickbuild binds
* Sniper: option to disable the crosshair when zooming in, option to change the zoomed in sensitivity, option to sound a bell when the sniper rifle is charged
* Zoom in bind (changes fov+sensitivity to zoom in)
* P-Rec settings

Installation
-------------

* Download the program [here](https://github.com/downloads/logophobia/YetAnotherTF2ConfigGenerator/YATF2ConfigGenerator-0.10.zip)
* Make sure you have a new version of the java runtime (1.6 or higher would probably work) installed, [this is a version without toolbar crap](http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1637588.html)
* Unzip the program and double click on the jar file to start
* Set everything up the way you want to, make sure you copy most of your essential settings in tf2, like your mouse sensitivity (very annoying when that gets changed)
* Select your steam directory and username and save the configuration files to tf2
* Save the settings for further editing if you might want to change some small things later
* Use the backup button to make a backup if you have any scripts you might like to preserve

Uninstallation
---------------

* Delete all the .cfg files inside the tf/cfg directory
* Run tf2 once with the -autoconfig flag (inside the launch options) to reset tf2 to default
* Place any backed-up cfg files back

FAQ
----

I am having issues, it crashes. How can I fix this?

Try the following things:
* Make sure you unzip your program before running it, otherwise it won't be able to find any of the files it needs
* Upgrade java to its latest version (from the link provided in the installation instructions), earlier java version don't work very well with this program
* Try to generate the scripts locally, and copying them manually the the tf/cfg directory in the team fortress installation directory1
* Post an explanation + screenshot (if applicable) with the crash/issue on my github page: github.com/logophobia/YetAnotherTF2ConfigGenerator or add me at steamcommunity.com/id/logophobia

What is viewmodel switch mode?

It indicates when the viewmodel is turned off if that is set for the weapon slot:
* Immediately after switching weapons
* After firing a weapon
* After switching strafe directions
* After either firing or strafing

I have a feature request, where can I post it?

Post any suggestions on the github page

What does medic radar do precisely?

It toggles medic autocall to 150%, so each of your teammates has a medic symbol above their heads, which you can see through walls. As soon as you let go of the button, it will go back to its original autocall level

How can I customize this even more?

You could try editing the original templates, in the template directory. First, delete the cache directory. Then edit the templates, the language used is ssp: http://scalate.fusesource.org/documentation/ssp-reference.html. After that, the program will use your editted version of the templates. 

What other ways to customize tf2 are there?

You can install custom huds, skins, hitsounds or customize/make your own scripts

My ambassador crosshair disappeared, how can I fix this?

For some reason, the crosshair after shooting is about half the default size. Increasing the size of the spy primary crosshair should fix this.

Changing settings inside tf2 doesn't work, it just gets changed back. How can I change my settings?

The scripts that were installed now regulate most of your settings inside tf2. Change stuff through the program, and then reinstall the scripts, or uninstall the scripts and be free to change things inside tf2 again.

Todo (1.0-RC)
-----

* create license
* show all crosshairs, even default ones based on class
* Prehaps drop P-Rec support in the future (due to disappearance plugin online)
* Make dialogs copyable
* Other requests?

Testing todo (1.0-RC)
-----

* Create a "base" set of profiles, options:
  * "Exactly" the same as normal tf2
  * Profile for nice default crosshair settings
  * WASD/ESDF/RDFG layouts, competative oriented
  * Spy/Medic/Engineer oriented binds
* Tutorial video
* Test a lot more
  

Known Bugs
-----------

* When building as an engineer, if you start building, then switch weapons before putting the building down, the crosshairs will get out of sync, switch weapons once to fix

Version history
----------------

Version 0.2
* Zoom bind
* Ability to "not" bind certain things
* P-Rec settings
* Pop-up with information about the graphics config (if any) you selected
* Ability to turn of dingalings on weapon/class basis
* Reset button that resets almost all of the stuff the config does, the rest can be done by resetting the keys from ingame

Version 0.3
* Scripts should generate quite a bit faster now
* Added binds for friendly spy disguises

Version 0.4
* Create cfg directory if it doesn't exist
* Added a couple of missing keys
* Ability to unbind stuff
* Fixed couple bugs

Version 0.5
* Fixed bugs
* Save progress button
* Better error reporting
* Account for some upper case file names in the steam directory

Version 0.6
* Added nextinv/previnv/prevweapon as possible binds, cleanup of weapon switching code while I was at it
* Some additional graphical options
* Some binds that shouldve been there (accept reject popup, toggle friendly disguise)
* Fixed some engineer stuff, hauling buildings doesn't desync stuff anymore

Version 0.7

* No more "nothing" error messages on the tf2 console
* Spawn toggling

Version 0.8
* Crosshair previews
* Config backup button
* Tweakable viewmodel behaviour
* Hud reload fixes visual glitches as well now
* Steam dir/username are saved

Version 0.9
* Fix the class-specific interp-ratio

Version 0.10
* Bind for spectator switching
* Ability to set text automatically chatted when popping/faking uber
* Integrated viewmodel switch mode a bit more into some medic/spy/engineer stuff that rebound attack
* Added option to show viewmodel when cloaking/dring or not
* Removed "Show text when ubering" option, just remove the text from the new options

Version 0.10.1
* Bind for the MvM ready toggle

Version 0.11 (not released yet)
* cleanup backend + GUI
* Field validation, bind conflict checking
* Loadable profiles with settings
* Some options are now settable for each class/weapon
* Extended backup system
* Option to enter custom scripts for autoexec or each class seperate
