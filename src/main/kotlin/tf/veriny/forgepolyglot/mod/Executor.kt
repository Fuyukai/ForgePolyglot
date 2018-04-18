/**
 * This file is part of ForgePolyglot.
 *
 * ForgePolyglot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ForgePolyglot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ForgePolyglot.  If not, see <http://www.gnu.org/licenses/>.
 */
package tf.veriny.forgepolyglot.mod

import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Engine
import org.graalvm.polyglot.Value
import tf.veriny.forgepolyglot.util.Logging
import java.io.File
import java.io.FileInputStream
import java.util.*

/*
 * Some notes:
 *
 *  1) The polymod.properties is used to define the language type, since the ModMetadata object
 *     has absolutely no ability to store extra information, so we use the language type to
 *     get mod information, then provide the metadata anyway.
 *
 *  2) This class is basically the mod barrier - it provides the way for FML to get into the
 *     underlying mod. It is technically an executor, too, hence the name.
 *
 *  3) As far as I can tell, we cannot fix FML to let us do our own discovery - allowing us to
 *     inject the mods at the proper stage. So, instead, we have to inject the mods when
 *     ForgePolygot is constructed, which is a bit late.
 */

/**
 * Represents an mod for a Polyglot mod.
 */
class Executor(val modDirectory: File) {
    companion object {
        // todo: Possibly customize engine?
        val engine = Engine.newBuilder().build()

        val languageMapping = mapOf(
            "python" to "py",
            "js" to "js"
        )
    }

    // the context is made from the current engine, meaning that it's a bit faster
    // as it shares some ast stuff etc
    private val context = Context.newBuilder().engine(engine)
        // change some options
        .allowCreateThread(false).allowHostAccess(true)
        .build()!!

    var properties: Properties
    // property getters
    val language: String
        get() = this.properties.getProperty("language")

    // the actual mod object
    private val _wrappedModObject: Value

    init {
        val modInfoPath = this.modDirectory.absolutePath + "/polymod.properties"
        // load in properties
        this.properties = Properties()
        this.properties.load(FileInputStream(modInfoPath))

        assert(language in languageMapping, { "language is not supported" })
        val ext = languageMapping[this.language]

        val mainFile = this.properties.getProperty("main-file", "mod.$ext")
        // attempt to load the main file and execute it
        val path = this.modDirectory.absolutePath + "/$mainFile"
        Logging.info { "Attempting to load polyglot mod $path" }

        val file = File(path)
        this.context.eval(this.language, file.readText(charset = Charsets.UTF_8))
        // now we try and get main
        Logging.info { "Loading succeeded, trying to get the main function..." }
        val mainCallable = this.properties.getProperty("main-object", "main")
        val main = this.context.polyglotBindings.getMember(mainCallable)
        // call main to get our object
        if (!main.canExecute()) {
            throw RuntimeException("Got non-callable main! Cannot construct mod.")
        }
        this._wrappedModObject = main.execute()
    }

    // THE BARRIER
    // This is the guardian between the mod code and everybody else.

    /**
     * Executes a function on the wrapped mod object.
     *
     * @param name: The name of the function to execute.
     * @return The return Value of the function.
     */
    fun executeFunction(name: String, vararg args: Any?): Value {
        val executable = this._wrappedModObject.getMember(name)
        if (!executable.canExecute()) {
            throw RuntimeException("This is not a function")
        }
        return executable.execute(*args)
    }
}