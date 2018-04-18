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
package tf.veriny.forgepolyglot.util

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Helper for logging. This will only log if said log levels are enabled.
 */
object Logging {
    val logger: Logger = LogManager.getLogger("ForgePolyglot")

    /**
     * Logs an info message.
     */
    inline fun info(msg: () -> String) {
        if (this.logger.isInfoEnabled) this.logger.info(msg())
    }

    inline fun warning(msg: () -> String) {
        if (this.logger.isWarnEnabled) this.logger.warn(msg())
    }

    inline fun error(msg: () -> String) {
        if (this.logger.isErrorEnabled) this.logger.error(msg())
    }

    inline fun debug(msg: () -> String) {
        if (this.logger.isDebugEnabled) this.logger.debug(msg())
    }

}
