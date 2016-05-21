# SLogic
This program was made to make it easy to construct digital circuits of varying complexity. When small circuits are constructed you can simply place all the components you need and link them together, and when you build advanced systems you can easily construct it part-by-part as module blocks and them connect them all together. SLogic was built with an emphasis on multi-user support, so every time the program is launched it automatically hosts a server which allows other users to connect and contribute to the circuit. It is possible to lock the server using a password and only allow others to view the circuit when they don't know the password.

## Features
The following is a list summarizing the main features of SLogic:
 * __Multi-user support:__ Work on circuits together with others!
 * __Modularize circuits:__ Split large circuits into smaller modules to make your circuit easier to work with.
 * __Customizable editor:__ The panels in the editor are resizable, it supports zooming and there are a lot of settings to play with (display language, grid options, etcetera...).
 * __Customizable components:__ Change color of LEDs, toggle buttons vs. push buttons, change clock delays, US vs. IEC gate standard, etcetera...
 * __Moving and copying:__ There are tools to move and copy existing components however you please.
 
### Multi-user support
As stated previously SLogic always runs on a server which allows other users to join and contribute to your circuit. By default anyone can join and edit the circuit, but this can be changed by setting a password in the settings. It is also possible to allow users who don't know the password to connect in a read-only mode.

When you open the program or disconnect from a server SLogic will automatically host a server with you set as the host. This means that only you will be able to load circuits, create new circuit files and choose where they are to be saved (although other users will be able to save the circuit once you have selected a save file for it). Furthermore, it is only the host who may create a module from the current circuit.

When connected to a server, either as a client or as a host, you will be identified by your username. The username can be chosen in the settings or when connecting to another user. You can send chat messages in the chat/status window at the bottom of the program, which helps you communicate. You can also see the cursors of other users in the editor.

### Modularize circuits
When an advanced system is to be constructed, or a collection of components are to be duplicated it may be a good idea to structure it by using modules. Modules are created by constructing a circuit soley consisting of the components you want to have in your module. Then you replace all inputs with module/clock inputs and all outputs with module outputs. The module inputs/outputs define where signals enter/exit your module. Each such component has a name which you may set by clicking on it, or by typing it in when the module is created. When all inputs and outputs are placed you can create the module by clicking the correct button in the toolbar.

In the module dialog which pops up when you create a module you can rename all the inputs/outputs and resize the module until it looks like you want. You should also assign a name to the module and a short description describing what the input and output pins do. When the module is created it is added to the list of modules in the component panel. All modules you have created can be used in any circuit that you create in SLogic, and when other users connect to you all your modules will be transfered to them so that they may use them while they are on your server.

If you want to see examples of module circuits or if you just are interested you can take a look at the default modules whose source files are located in the folder circuits/modules (there are also tests to ensure their functionality in circuits/modules/tests).

## Compiling
To compile the program you first need to download the source files for the libraries specified in the Dependencies section. When all the source files have been obtained there should be no problem compiling the program. Once it is compiled it can be run by launching the class LogicSim (in the gui-package).

If you want to create a runnable jar-file, just include all the compiled class files and create an appropriate manifest file which points to gui.LogicSim as the main class. Once the jar-file has been made it will need the folders "textures", "languages" and "modules" in the same folder as the program for it to run correctly. 

## Dependencies
To compile the program you will need [SNet](https://github.com/Sebastian-0/SNet) as well as my [Utilities library](https://github.com/Sebastian-0/Utilities).

## License
This networking library is free to use as long as you comply to the GNU GPL v3 license (see LICENSE for details).
