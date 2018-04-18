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
import tf.veriny.forgepolyglot.util.Logging
import java.io.File
import java.io.FileInputStream
import java.util.*

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

    lateinit var language: String

    /**
     * Loads the mod into a ModContainer.
     */
    fun load() {
        val modInfoPath = this.modDirectory.absolutePath + "/polymod.properties"
        val props = Properties()
        props.load(FileInputStream(modInfoPath))

        this.language = props.getProperty("language")
        assert(language in languageMapping, { "language is not supported" })
        val ext = languageMapping[this.language]

        val mainFile = props.getProperty("main_file", "mod.$ext")
        // attempt to load the main file and execute it
        val path = this.modDirectory.absolutePath + "/$mainFile"
        Logging.info { "Attempting to load polyglot mod $path" }

        val file = File(path)
        this.context.eval(this.language, file.readText(charset = Charsets.UTF_8))
        // now we try and get main
        Logging.info { "Loading succeeded, trying to get the main function..." }
        val main = this.context.polyglotBindings.getMember("main")

    }
}