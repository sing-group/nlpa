package org.ski4spam.pipe.impl;

import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;

import java.io.Reader;
import java.io.Serializable;
import java.io.IOException;
import java.io.Writer;

import org.w3c.tidy.Tidy;
/**
 * This pipe drops HTML tags and changes entities by their corresponding character
 * @author José Ramón Méndez Reboredo
 */
public class StripHTMLFromStringBufferPipe extends Pipe {

    public StripHTMLFromStringBufferPipe() {
    }


    @Override
    public Instance pipe(Instance carrier) {
        if ( carrier.getData() instanceof StringBuffer){
			 Tidy tidy = new Tidy();
			 StringBuffer newSb=new StringBuffer();
			 
			 Reader in = new CharSequenceReader((StringBuffer)(carrier.getData()));
			 Writer out=new StringBufferWriter(newSb); 
				
			 tidy.parse(in,out);
			 
			 try{
			    in.close();
			    out.close();
		     }catch(IOException e){
				 System.out.println(e);
				 e.printStackTrace();
		     }
				        
			 carrier.setData(newSb);
		}

        return carrier;
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * {@link Reader} implementation that can read from String, StringBuffer,
 * StringBuilder or CharBuffer.
 * <p>
 * <strong>Note: Supports {@link #mark(int)} and {@link #reset()}.
 *
 * @version $Revision: 659817 $ $Date: 2008-05-24 14:23:10 +0100 (Sat, 24 May 2008) $
 * @since Commons IO 1.4
 */
class CharSequenceReader extends Reader implements Serializable {

    private final CharSequence charSequence;
    private int idx;
    private int mark;

    /**
     * Construct a new instance with the specified character sequence.
     *
     * @param charSequence The character sequence, may be <code>null
     */
    public CharSequenceReader(CharSequence charSequence) {
        this.charSequence = (charSequence != null ? charSequence : "");
    }

    /**
     * Close resets the file back to the start and removes any marked position.
     */
    @Override
    public void close() {
        idx = 0;
        mark = 0;
    }

    /**
     * Mark the current position.
     *
     * @param readAheadLimit ignored
     */
    @Override
    public void mark(int readAheadLimit) {
        mark = idx;
    }

    /**
     * Mark is supported (returns true).
     *
     * @return <code>true
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * Read a single character.
     *
     * @return the next character from the character sequence
     * or -1 if the end has been reached.
     */
    @Override
    public int read() {
        if (idx >= charSequence.length()) {
            return -1;
        } else {
            return charSequence.charAt(idx++);
        }
    }

    /**
     * Read the sepcified number of characters into the array.
     *
     * @param array The array to store the characters in
     * @param offset The starting position in the array to store
     * @param length The maximum number of characters to read
     * @return The number of characters read or -1 if there are
     * no more
     */
    @Override
    public int read(char[] array, int offset, int length) {
        if (idx >= charSequence.length()) {
            return -1;
        }
        if (array == null) {
            throw new NullPointerException("Character array is missing");
        }
        if (length < 0 || (offset + length) > array.length) {
            throw new IndexOutOfBoundsException("Array Size=" + array.length +
                    ", offset=" + offset + ", length=" + length);
        }
        int count = 0;
        for (int i = 0; i < length; i++) {
            int c = read();
            if (c == -1) {
                return count;
            }
            array[offset + i] = (char)c;
            count++;
        }
        return count;
    }

    /**
     * Reset the reader to the last marked position (or the beginning if
     * mark has not been called).
     */
    @Override
    public void reset() {
        idx = mark;
    }

    /**
     * Skip the specified number of characters.
     *
     * @param n The number of characters to skip
     * @return The actual number of characters skipped
     */
    @Override
    public long skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(
                    "Number of characters to skip is less than zero: " + n);
        }
        if (idx >= charSequence.length()) {
            return -1;
        }
        int dest = (int)Math.min(charSequence.length(), (idx + n));
        int count = dest - idx;
        idx = dest;
        return count;
    }

    /**
     * Return a String representation of the underlying
     * character sequence.
     *
     * @return The contents of the character sequence
     */
    @Override
    public String toString() {
        return charSequence.toString();
    }
}

/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
/**
 * This class codes around a silly limitation of StringWriter which doesn't allow a StringBuffer
 * to be passed in as a constructor for some bizarre reason.
 * So we replicate the behaviour of StringWriter here but allow a StringBuffer to be passed in.
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision$
 */
class StringBufferWriter extends Writer {

    private StringBuffer buffer;

    /**
     * Create a new string writer which will append the text to the given StringBuffer
     */
    public StringBufferWriter(StringBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Write a single character.
     */
    public void write(int c) {
        buffer.append((char) c);
    }

    /**
     * Write a portion of an array of characters.
     *
     * @param text Array of characters
     * @param offset Offset from which to start writing characters
     * @param length Number of characters to write
     */
    public void write(char text[], int offset, int length) {
        if ((offset < 0) || (offset > text.length) || (length < 0) || ((offset + length) > text.length) || ((offset + length) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        else if (length == 0) {
            return;
        }
        buffer.append(text, offset, length);
    }

    /**
     * Write a string.
     */
    public void write(String text) {
        buffer.append(text);
    }

    /**
     * Write a portion of a string.
     *
     * @param text the text to be written
     * @param offset offset from which to start writing characters
     * @param length Number of characters to write
     */
    public void write(String text, int offset, int length) {
        buffer.append(text.substring(offset, offset + length));
    }

    /**
     * Return the buffer's current value as a string.
     */
    public String toString() {
        return buffer.toString();
    }

    /**
     * Flush the stream.
     */
    public void flush() {
    }

    /**
     * Closing a <tt>StringWriter</tt> has no effect. The methods in this
     * class can be called after the stream has been closed without generating
     * an <tt>IOException</tt>.
     */
    public void close() throws IOException {
    }
}