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
package tf.veriny.forgepolyglot

import org.graalvm.polyglot.Value

/**
 * Defines exceptions for ForgePolyglot.
 */

class MissingFieldError(field: String, wrapped: Value) :
    Exception("Missing field $field on object $wrapped!")

class InvalidFieldError(field: String, reason: String, value: Value)
    : Exception("Invalid field $field on object $value: $reason")


fun Value.getMemberOrError(name: String): Value {
    if (!this.hasMember(name)) throw MissingFieldError(name, this)
    return this.getMember(name)
}
