Virtual Optics
====================

[Virtual Optics in action!] (images/Snapshot.png)

Contents
--------------------

- Intro
- Quick Start
- Credits
- Issues and TODOs

Intro
---------------------
Virtual Optics is an optical simulation application developed in Java. It was created as part of an integrative school project,
where the aim was to build an educative application about a particular science topic. We chose to do it on the physics of optics.

Our goal was to produce an application that gives more freedom and more features than your typical optics bench applet that
restrains the user to one axis, few optical objects, and no ability to customize those objects. Virtual Optics offers an infinite
two dimensional plane, three modes (StoryLine, Learning Center and Laboratory), the ability to save/load optical setups, the 
ability to use many light sources at once, and eight types of objects which can be rotated, resized, colored and more. 
Please give it a try!

See Virtual Optics - Final report.docx (p.9 Figure 1 and p.20 Figure 13 give a summary of the class structure) and the Javadocs 
in the doc folder for more detailed information.

Quick Start
---------------------

Download the project zip file and extract the contents. Double click VirtualOptics.jar (WARNING: Please do not move the jar file)
to launch the application. Click "Laboratory" to head over to the lab section. To learn the controls, click the top left menu icon
and then the ctrl icon.

Pro-tip 1: When an object is out of view, a little translucent square marker appears on the edge of the window in the direction
			where that object can be found. To center the view on this object, right click on its marker. Alternatively, hold the
			ctrl key and "pull" the view by dragging the mouse in the opposite direction.

Try this:

Load the example.op file from the in-lab menu to see a pre-made optical setup demo (you can make your own by clicking the save icon
in the menu once you are satisfied with your setup). Only .op, for **op**tics, files are shown in the file chooser window. Once the
demo file is loaded, you see two curved mirrors and a light source (black dot at the top right). Double click the light source.
Isn't that awesome? The reflections in the curved mirrors are generated in real-time by our algorithm. Move the components to see
for yourself. Explore other locations by right clicking the square markers.

Pro-tip 2: Hold down the alt key and drag the mouse to select multiple objects at once. You can also hold down the shift key and 
			double click components you want to be selected one after the other. When more than one object is selected at once,
			they can be rotated/resized/deleted/activated simultaneously.


Credits
---------------------
Developed by Tieme Togola and Darrin Fong. Tieme was responsible for the gameComponents package, while Darrin focused on the 
userInterface package. Both contributed on the Lab class, with Tieme taking care of the event handling and Darrin the scroll panel
containing the objects on the right edge of the lab window.

Issues and TODOs
---------------------

- The jar file does not work in Ubuntu and cannot be moved. We need a standalone jar which can also run on Linux (will fix soon)

- In Ubuntu (run from Eclipse for now), the alt key is reserved for operating system functionalities and hides our features. Also
	some images won't display (use getResource instead of new File(...))

- Some minor glitches occur from time to time where light rays will pass through objects. Issue with intersection methods, fix this.

- We handle object collisions with bounding boxes (shown in yellow dotted lines). When the user causes objects to overlap, the 
	program will try to reposition objects to avoid the issue, and load a backup file if it fails at this repeatedly. Find a better
	way to handle this as it may cause the application to crash.

- Change the tag "Storyline" to "Game" or "Challenge" maybe. The story is no longer relevant.

- One can run LStest.java in the userInterface package and select the progress file from the pop-up window to reset the user level 
	progress to 1. Provide an easier way for the user to do this.

- When a marker is at the top left corner (where the in-game menu icon is located), it cannot be activated. The menu pops-up. Fix 
	this.
