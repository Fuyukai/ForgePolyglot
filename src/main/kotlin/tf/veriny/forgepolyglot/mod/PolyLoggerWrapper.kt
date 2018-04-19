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

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.OutputStream
import java.io.PrintStream

/**
 * Represents a logger wrapper.
 *
 * This will redirect stdout and stderr
 */
class PolyLoggerWrapper(val modId: String, val mode: String) : OutputStream() {
    val logger: Logger

    val loggerDelegate: (String) -> Unit

    init {
        val loggerName = "PolyMod-$modId-$mode"
        this.logger = LogManager.getLogger(loggerName)
        when (mode) {
            "stdout" -> this.loggerDelegate = this.logger::info
            "stderr" -> this.loggerDelegate = this.logger::error
            else -> this.loggerDelegate = this.logger::warn
        }
    }

    /**
     * The actual log function.
     */
    private fun doLog(s: String) {
        // this just removes the extra newline added from print calls
        val toLog = if (s.last() == '\n') s.dropLast(1) else s
        this.loggerDelegate(toLog)
    }

    // never call this
    override fun write(b: Int) {
        val char = b.toByte().toChar().toString()
        this.loggerDelegate(char)
    }

    override fun write(b: ByteArray) {
        val str = String(b)
        this.loggerDelegate(str)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        val nArr = b.copyOfRange(off, off+len)
        this.write(nArr)
    }
}