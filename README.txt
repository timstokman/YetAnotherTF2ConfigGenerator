Yet Another TF2 Config Generator

This program generates configuration files for team fortress 2

Features:

* Ability to specify for each weapon slot (or even each class if you want) the:
  * Crosshair (color, type, scale)
  * Sensitivity
  * Showing/Hiding the weapon model
  * Dingeling (volume, max/min pitch)
  * When you switch weapons (or go invisible), it will show the viewmodel for a short time, before turning it off if you've set it that way for the weapon. (It uses the changing of strafe directions for this, not the wait command)
* Null-movement canceling: if you press two opposite strafe keys, you will no longer stand still
* Most of the useful advanced options you can already find in the tf2 menu (autoreload, fastswitch, damagenumbers, etc)
* Can include chris' network/graphics config
* Option to hide the tracers, flames or medic healbeam (can be distracting)
* Turn gibs/ragdolls/sprays on/off
* Crouchjump bind, with the option to exclude it for certain classes (default excluded for demoman/soldier
* Team-only voice bind
* Bind for quick-switching weapon loadouts
* Bind for toggling to a forward spawn (if you want to retain uber or types of charge, switch weapons with the quickswitch bind instead)
* Binds for primary/secondary, current/melee, previous weapon, next inventory and previous inventory
* Bind for suicide and reloading the hud
* option to show a netgraph with the scoreboard to identify network issues
* Medic: Medic radar bind, fake uber bind, option to communicate ubers automatically through chat
* Spy: A disguise cycle bind (separate friendly and enemy disguise cycle) which cycles through a list of predefined disguises. Quickdisguise binds (enemy and friendly)
* Engineer: Quickbuild binds (default on the f-keys)
* Sniper: option to disable the crosshair when zooming in, option to change the zoomed in sensitivity, option to sound a bell when the sniper rifle is charged
* Ability to change the templates
* Zoom in bind (changes fov+sensitivity to zoom in on an area)
* P-Rec settings

Todo:

* Conflicting binds detection
* Crosshair images
* Backup config system
* Make the GUI a bit prettier
* Shrink/Clean up the templates a bit
* Option to have an affect similar to stabby's crosshair script (changing crosshair colors when strafing), or a switching the crosshair color when firing your weapon
* Other requests?

Known Bugs:

* Weird localized steam directory with translated folder names can give some trouble when writing the config files to the tf2 directory 
* When building as an engineer, if you start building, then switch weapons before putting the building down, the crosshairs will get out of sync 

Installation:

* Make sure you have the java runtime installed, this is a version without toolbar crap: http://www.oracle.com/technetwork/java/javase/downloads/jre-7u4-download-1591157.html
* Unzip the program and double click on the jar file to start
* Generating the scripts can take a bit long, especially on older pcs, be patient and wait for the popup

Changes:

Version 0.2:

* Zoom bind
* Ability to "not" bind certain things
* P-Rec settings
* Pop-up with information about the graphics config (if any) you selected
* Ability to turn of dingalings on weapon/class basis
* Reset button that resets almost all of the stuff the config does, the rest can be done by resetting the keys from ingame

Version 0.3:

* Scripts should generate quite a bit faster now
* Added binds for friendly spy disguises

Version 0.4:

* Create cfg directory if it doesn't exist
* Added a couple of missing keys
* Ability to unbind stuff
* Fixed couple bugs

Version 0.5:

* Fixed bugs
* Save progress button
* Better error reporting
* Account for some upper case file names in the steam directory

Version 0.6:

* Added nextinv/previnv/prevweapon as possible binds, cleanup of weapon switching code while I was at it
* Some additional graphical options
* Some binds that shouldve been there (accept reject popup, toggle friendly disguise)
* Fixed some engineer stuff, hauling buildings doesn't desync stuff anymore

Version 0.7:

* No more "nothing" error messages on the tf2 console
* Spawn toggling
