/*
 * Redberry: symbolic tensor computations.
 *
 * Copyright (c) 2010-2013:
 *   Stanislav Poslavsky   <stvlpos@mail.ru>
 *   Bolotin Dmitriy       <bolotin.dmitriy@gmail.com>
 *
 * This file is part of Redberry.
 *
 * Redberry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Redberry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Redberry. If not, see <http://www.gnu.org/licenses/>.
 */
package cc.redberry.core.parser;

/**
 * Parser for Redberry {@link cc.redberry.core.tensor.Expression}.
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 * @since 1.0
 */
public class ParserExpression implements TokenParser {
    /**
     * Singleton instance.
     */
    public static final ParserExpression INSTANCE = new ParserExpression();

    private ParserExpression() {
    }

    @Override
    public ParseToken parseToken(String expression, Parser parser) {
        if (!expression.contains("="))
            return null;
        if (expression.indexOf('=') != expression.lastIndexOf('='))
            throw new ParserException("Several '=' symbols.");
        String[] parts = expression.split("=");
        ParseToken left = parser.parse(parts[0]);
        ParseToken right = parser.parse(parts[1]);
        return new ParseToken(TokenType.Expression, left, right);
    }

    @Override
    public int priority() {
        return 10100;
    }
}
