/**
 * Copyright (C) 2011 rwoo@gmx.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.streamflyer.experimental.bytestream;

import java.awt.Font;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.core.ModifyingWriter;

/**
 * This character comprises 256 characters, each of them corresponds to another
 * byte value, i.e. each character can be encoded via one byte.
 * <p>
 * The characters are all 'visual', i.e. control characters like '\n', '\t' do
 * not belong to this character set, and the character set contains only
 * characters that are likely to be rendered by the most common fonts.
 * <p>
 * Therefore, most characters encodings are taken from
 * http://en.wikipedia.org/wiki/Windows-1252 (java encoding name is
 * "windows-1252"). The encodings that deviate from the Windows-1252 are the
 * following.
 * <ul>
 * <li>0x TODO
 * <li>
 * <li>
 * </ul>
 * <p>
 * <p>
 * TODO could the be redundant by using {@link CharBuffer} instead of
 * {@link StringBuilder} in {@link ModifyingReader} and {@link ModifyingWriter}
 * ?
 * 
 * @author rwoo
 * 
 * @since 15.09.2011
 */
public class VisualCharset extends Charset {

    //
    // inner classes
    //

    public static class VisualCharsetDecoder extends CharsetDecoder {

        protected VisualCharsetDecoder() {
            super(VisualCharset.getInstance(), 1.0f, 1.0f);
        }

        /**
         * @see java.nio.charset.CharsetDecoder#decodeLoop(java.nio.ByteBuffer,
         *      java.nio.CharBuffer)
         */
        @Override
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {


            // TODO Auto-generated method stub
            return null;
        }
    }

    public static class VisualCharsetEncoder extends CharsetEncoder {

        protected VisualCharsetEncoder() {
            super(VisualCharset.getInstance(), 1.0f, 1.0f);
        }

        /**
         * @see java.nio.charset.CharsetEncoder#encodeLoop(java.nio.CharBuffer,
         *      java.nio.ByteBuffer)
         */
        @Override
        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            // TODO Auto-generated method stub
            return null;
        }

    }


    //
    // singleton
    //

    private static VisualCharset instance;


    public synchronized static VisualCharset getInstance() {
        if (instance == null) {
            instance = new VisualCharset();
        }
        return instance;
    }

    //
    // constructors
    //

    protected VisualCharset() {
        super("visual-1252", null);

    }


    //
    // override Charset.*
    //

    /**
     * @see java.nio.charset.Charset#contains(java.nio.charset.Charset)
     */
    @Override
    public boolean contains(Charset cs) {
        return cs instanceof VisualCharset;
    }

    /**
     * @see java.nio.charset.Charset#newDecoder()
     */
    @Override
    public CharsetDecoder newDecoder() {
        return new VisualCharsetDecoder();
    }

    /**
     * @see java.nio.charset.Charset#newEncoder()
     */
    @Override
    public CharsetEncoder newEncoder() {
        return new VisualCharsetEncoder();
    }

    //
    // main
    //

    public static void main(String[] args) throws Exception {
        // print the characters
        // TODO print (1) javadoc, (2) print encoding tables

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; i++) {

            if (i % 16 == 0 && sb.length() != 0) {
                sb.append('\n');
            }

            char ch;

            if (i < 0x20 || i == 0x7F || i == 0x81 || i == 0x8D || i == 0x8F
                    || i == 0x90 || i == 0x9D || i == 0xA0) {
                // 7F DELETE
                // 81, 8D, 8F, 90, 9D not defined in windows-1252
                // A0 NON BREAKING SPACE (visually not distinguishable from
                // SPACE)
                // should I replace some dashes as well?
                if (i < 0x1F) {
                    ch = (char) (0x03B0 + i);
                }
                else {
                    switch (i) {
                    case 0x1F:
                        ch = 0x03A6; // Greek upper-case letter PSI(?)
                        break;
                    case 0x7F:
                        ch = 0x0393; // Greek upper-case letter GAMMA
                        break;
                    case 0x81:
                        ch = 0x0394; // Greek upper-case letter DELTA
                        break;
                    case 0x8D:
                        ch = 0x0398; // Greek upper-case letter OMIKRON
                        break;
                    case 0x8F:
                        ch = 0x039B; // Greek upper-case letter LAMBDA
                        break;
                    case 0x90:
                        ch = 0x039E; // Greek upper-case letter ETA(?)
                        break;
                    case 0x9D:
                        ch = 0x03A0; // Greek upper-case letter PI
                        break;
                    case 0xA0:
                        ch = 0x03A3; // Greek upper-case letter SIGMA
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "oops, unknown value: " + i
                                        + ". typo somewhere?");
                    }
                }
            }
            else {
                byte b = (byte) i;
                // (historical name for windows-1252 is Cp1252)
                ch = new String(new byte[] { b }, "windows-1252").charAt(0);
            }

            // append character
            sb.append(ch);
        }

        SwingingPrintStream out = new SwingingPrintStream();
        out.println(sb);
    }

    private static class SwingingPrintStream {

        private JTextComponent textComponent;

        public SwingingPrintStream() {
            super();

            JFrame frame = new JFrame(getClass().getName());
            textComponent = new JTextArea();
            // I tried some fonts.
            // no fixed-width: Dialog
            // partially fixed-width: DialogInput, Monospaced, Courier
            // fixed-width: Lucida Sans Typewriter, Lucida Console, Courier New
            textComponent.setFont(new Font("Courier New", Font.PLAIN, 16));
            frame.getContentPane().add(textComponent);
            frame.setVisible(true);
        }

        public void println(Object obj) {
            textComponent.setText(textComponent.getText() + obj + "\n");
        }

        // add closing handler

    }
}