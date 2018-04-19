Forge Polyglot
--------------

Forge Polyglot is a proof of concept mod loader running on top of FML. It allows the usage of the
Graal VM to write mods in different languages on top of the JVM.

Requirements
============

Forge Polyglot requires `Forgelin <https://github.com/shadowfacts/Forgelin>`__ and the Graal VM
to load other languages.

Usage
=====

First, make a new folder in your ``mods/`` directory called ``polymods``, and make a new
directory inside that for your mod.

You need 3 files for a Polymod to work:

    - A ``polymod.properties`` file which has the Mod ID and language

    - An ``mcmod.info`` file which is used for mod metadata

    - A ``main.py`` (or main.js, or whatever) which will be your mod file.

Start by making your properties file:

.. code-block:: properties

    # The Mod ID for your mod
    modid=testmod
    # The language for your mod
    language=python
    # The main file for your mod
    main-file=testmod.py

The ``mcmod.info`` is a standard mcmod.info file - just copy it from a normal mod or something.

The main file can then be written in your language of choice; but it must have some form of
structure:

    - The main file must have a function called ``main`` which takes 0 arguments and returns your
      main object.

    - You must return a main object.

TODO: Everything else

Licence
=======

Forge Polyglot is licenced under the GPL v3.
