Yet Another TF2 Config Generator

This program generates configuration files for team fortress 2

Features:
* Ability to specify for each weapon slot (or even each class if you want) the:
  * Crosshair (color, type, scale)
  * Sensitivity (by default all the same, possible to fuck with muscle memory that way, but some people prefer it)
  * Showing/Hiding the weapon model
  * Dingeling (for example, the sniper rifle has higher max damage then the scout bat, you're able to specify for each slot/class the max and min damage pitch and volume)
  * The special twist here is, that when switching the weapon it will always show the weapon model at first, then, when you switch strafe 
    directions (to avoid using the wait command), it will hide (or not) the weapon depending on the settings for that slot.
* Null-movement canceling: if you press two opposite strafe keys, you will no longer stand still. enables you to get rid of "null-movement" while strafing.
* Option to set dingelings that vary based on how much damage you do
* Fastswitch/Autoreload/Damage number options
* Can include chris' network/graphics config
* Option to hide the tracers (the lines that show when you shoot a hitscan weapon, they can be distracting)
* Crouchjump bind, with the option to exclude it for certain classes (default excluded for demoman/soldier, because they need the ability to tab-crouch jump), makes it easier to trickjump or surf explosions
* Team-only voice bind
* Bind for quick-switching weapon loadouts
* Bind for switching between primary and secondary, or current weapon and melee weapon
* Bind for suicide and reloading the hud
* option to show a netgraph with the scoreboard to identify network issues
* you can change the normal binds as well
* Medic: Medic radar bind, fake uber bind, option to communicate ubers automatically through chat, option to hide or show the viewmodel healbeam
* Pyro: option to hide or show the viewmodel flames (they obscure your vision)
* Spy: A disguise cycle bind (separate friendly and enemy disguise cycle) which cycles through a list of predefined disguises. Direct binds for each disguise (default on the f-keys)
* Engineer: Quickbuild binds (default on the f-keys)
* Sniper: option to disable the crosshair when zooming in, option to change the zoomed in sensitivity, option to sound a bell when the sniper rifle is charged
* If you are brave, you can edit the templates and add your own stuff
* Zoom in bind 
* P-Rec settings

Todo:
* Spawntoggle bind
* Conflicting binds detection
* Backup config system
* Make the GUI a bit prettier
* Shrink/Clean up the templates a bit
* Option to have an affect similar to stabby's crosshair script (changing crosshair colors when strafing), or a switching the crosshair color when firing your weapon
* variable cl_inter_ratio for different classes (or even weapons?)
* Write a "none/null" keybind to the config in a way that doesn't write error messages to the tf2 console
* Other requests?

Known Bugs:
* For some people the config generator (not tf2) crashes in the background, report the full error message to me so I can fix it
* Weird localized steam directory with translated folder names can give some trouble when writing the config files to the tf2 directory 
* When building as an engineer, when you put down the building with primary fire, and while holding down that button, switch weapon, it will continue attack with that weapon until you put -attack in the console

Installation:
* Make sure you have the java runtime installed, this is a version without toolbar crap: http://www.oracle.com/technetwork/java/javase/downloads/jre-7u4-download-1591157.html
* Unzip the program and double click on the jar file to start
* Generating the scripts can take a bit long, especially on older pcs, be patient and wait for the popup
