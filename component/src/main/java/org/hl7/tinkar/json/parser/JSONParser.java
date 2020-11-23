/*
 * Copyright 2020 kec.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hl7.tinkar.json.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hl7.tinkar.json.JSONArray;
import org.hl7.tinkar.json.JSONObject;
import org.hl7.tinkar.uuid.UUIDUtil;


/**
 * Original obtained from: https://github.com/fangyidong/json-simple under Apache 2 license
 * Original project had no support for Java Platform Module System, and not updated for 8 years. 
 * Integrated here to integrate with Java Platform Module System. 
 * 
 * Parser for JSON text. Please note that JSONParser is NOT thread-safe.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public class JSONParser {
	public static final int S_INIT=0;
	public static final int S_IN_FINISHED_VALUE=1;//string,number,boolean,null,object,array
	public static final int S_IN_OBJECT=2;
	public static final int S_IN_ARRAY=3;
	public static final int S_PASSED_PAIR_KEY=4;
	public static final int S_IN_PAIR_VALUE=5;
	public static final int S_END=6;
	public static final int S_IN_ERROR=-1;
	
	private LinkedList handlerStatusStack;
	private final Yylex lexer = new Yylex((Reader)null);
	private Yytoken token = null;
	private int status = S_INIT;
	
	private int peekStatus(LinkedList statusStack){
		if(statusStack.isEmpty()) {
                    return -1;
                }
		return (Integer)statusStack.getFirst();
	}
	
    /**
     *  Reset the parser to the initial state without resetting the underlying reader.
     *
     */
    public void reset(){
        token = null;
        status = S_INIT;
        handlerStatusStack = null;
    }
    
    /**
     * Reset the parser to the initial state with a new character reader.
     * 
     * @param in - The new character reader.
     */
	public void reset(Reader in){
		lexer.yyreset(in);
		reset();
	}
	
	/**
	 * @return The position of the beginning of the current token.
	 */
	public int getPosition(){
		return lexer.getPosition();
	}
	
	public Object parse(String s) throws ParseException{
		return parse(s, (ContainerFactory)null);
	}
	
	public Object parse(String s, ContainerFactory containerFactory) throws ParseException{
		StringReader in=new StringReader(s);
		try{
			return parse(in, containerFactory);
		}
		catch(IOException ie){
			/*
			 * Actually it will never happen.
			 */
			throw new ParseException(-1, ParseException.ERROR_UNEXPECTED_EXCEPTION, ie);
		}
	}
	
	public Object parse(Reader in) throws IOException, ParseException{
		return parse(in, (ContainerFactory)null);
	}
	
	/**
	 * Parse JSON text into java object from the input source.
	 * 	
	 * @param in
     * @param containerFactory - Use this factory to createyour own JSON object and JSON array containers.
	 * @return Instance of the following:
	 *  org.hl7.tinkar.JSONObject,
	 * 	org.hl7.tinkar.JSONArray,
	 * 	java.lang.String,
	 * 	java.lang.Number,
	 * 	java.lang.Boolean,
	 * 	null
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public Object parse(Reader in, ContainerFactory containerFactory) throws IOException, ParseException{
		reset(in);
		LinkedList<Integer> statusStack = new LinkedList();
		LinkedList<Object> valueStack = new LinkedList();
		
		try{
			do{
				nextToken();
				switch(status){
				case S_INIT -> {
                                    switch(token.type){
                                        case Yytoken.TYPE_VALUE -> {
                                            status=S_IN_FINISHED_VALUE;
                                            statusStack.addFirst(status);
                                            valueStack.addFirst(token.value);
                                        }
                                        case Yytoken.TYPE_LEFT_BRACE -> {
                                            status=S_IN_OBJECT;
                                            statusStack.addFirst(status);
                                            valueStack.addFirst(createObjectContainer(containerFactory));
                                        }
                                        case Yytoken.TYPE_LEFT_SQUARE -> {
                                            status=S_IN_ARRAY;
                                            statusStack.addFirst(status);
                                            valueStack.addFirst(createArrayContainer(containerFactory));
                                        }
                                        default -> status=S_IN_ERROR;
                                    }//inner switch
                                }

					
				case S_IN_FINISHED_VALUE -> {
                                    if(token.type==Yytoken.TYPE_EOF)
                                        return valueStack.removeFirst();
                                    else
                                        throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
                                }
					
				case S_IN_OBJECT -> {
                                    switch(token.type){
                                        case Yytoken.TYPE_COMMA -> {
                                        }
                                        case Yytoken.TYPE_VALUE -> {
                                            if(token.value instanceof String key){
                                                valueStack.addFirst(key);
                                                status=S_PASSED_PAIR_KEY;
                                                statusStack.addFirst(status);
                                            }
                                            else{
                                                status=S_IN_ERROR;
                                            }
                                        }
                                        case Yytoken.TYPE_RIGHT_BRACE -> {
                                            if(valueStack.size()>1){
                                                statusStack.removeFirst();
                                                valueStack.removeFirst();
                                                status=peekStatus(statusStack);
                                            }
                                            else{
                                                status=S_IN_FINISHED_VALUE;
                                            }
                                        }
                                        default -> status=S_IN_ERROR;
                                    }//inner switch
                                }
					
				case S_PASSED_PAIR_KEY -> {
                                    switch(token.type){
                                        case Yytoken.TYPE_COLON -> {
                                        }
                                        case Yytoken.TYPE_VALUE -> {
                                            statusStack.removeFirst();
                                            String key=(String)valueStack.removeFirst();
                                            Map parent=(Map)valueStack.getFirst();
                                            parent.put(key,token.value);
                                            status=peekStatus(statusStack);
                                        }
                                        case Yytoken.TYPE_LEFT_SQUARE -> {
                                            statusStack.removeFirst();
                                            String key = (String)valueStack.removeFirst();
                                            Map parent = (Map)valueStack.getFirst();
                                            List newArray=createArrayContainer(containerFactory);
                                            parent.put(key,newArray);
                                            status=S_IN_ARRAY;
                                            statusStack.addFirst(status);
                                            valueStack.addFirst(newArray);
                                        }
                                        case Yytoken.TYPE_LEFT_BRACE -> {
                                            statusStack.removeFirst();
                                            String key = (String)valueStack.removeFirst();
                                            Map parent = (Map)valueStack.getFirst();
                                            Map newObject=createObjectContainer(containerFactory);
                                            parent.put(key,newObject);
                                            status=S_IN_OBJECT;
                                            statusStack.addFirst(status);
                                            valueStack.addFirst(newObject);
                                        }
                                        default -> status=S_IN_ERROR;
                                    }
                                }
					
				case S_IN_ARRAY -> {
                                    switch(token.type){
                                        case Yytoken.TYPE_COMMA -> {
                                        }
                                        case Yytoken.TYPE_VALUE -> {
                                            List val=(List)valueStack.getFirst();
                                            if (token.value instanceof String string && UUIDUtil.isUUID(string)) {
                                                val.add(UUID.fromString(string));
                                            } else {
                                                val.add(token.value);
                                            }
                                            
                                        }
                                        case Yytoken.TYPE_RIGHT_SQUARE -> {
                                            if(valueStack.size()>1){
                                                statusStack.removeFirst();
                                                valueStack.removeFirst();
                                                status=peekStatus(statusStack);
                                            }
                                            else{
                                                status=S_IN_FINISHED_VALUE;
                                            }
                                        }
                                        case Yytoken.TYPE_LEFT_BRACE -> {
                                            List val = (List)valueStack.getFirst();
                                            Map newObject=createObjectContainer(containerFactory);
                                            val.add(newObject);
                                            status=S_IN_OBJECT;
                                            statusStack.addFirst(status);
                                            valueStack.addFirst(newObject);
                                        }
                                        case Yytoken.TYPE_LEFT_SQUARE -> {
                                            List val = (List)valueStack.getFirst();
                                            List newArray=createArrayContainer(containerFactory);
                                            val.add(newArray);
                                            status=S_IN_ARRAY;
                                            statusStack.addFirst(status);
                                            valueStack.addFirst(newArray);
                                        }
                                        default -> status=S_IN_ERROR;
                                    }//inner switch
                                }
				case S_IN_ERROR -> throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
				}//switch
				if(status==S_IN_ERROR){
					throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
				}
			}while(token.type!=Yytoken.TYPE_EOF);
		} catch(IOException ie){
			throw ie;
		}
		
		throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
	}
	
	private void nextToken() throws ParseException, IOException{
		token = lexer.yylex();
		if(token == null)
			token = new Yytoken(Yytoken.TYPE_EOF, null);
	}
	
	private Map createObjectContainer(ContainerFactory containerFactory){
		if(containerFactory == null)
			return new JSONObject();
		Map m = containerFactory.createObjectContainer();
		
		if(m == null)
			return new JSONObject();
		return m;
	}
	
	private List createArrayContainer(ContainerFactory containerFactory){
		if(containerFactory == null)
			return new JSONArray();
		List l = containerFactory.creatArrayContainer();
		
		if(l == null)
			return new JSONArray();
		return l;
	}
	
	public void parse(String s, ContentHandler contentHandler) throws ParseException{
		parse(s, contentHandler, false);
	}
	
	public void parse(String s, ContentHandler contentHandler, boolean isResume) throws ParseException{
		StringReader in=new StringReader(s);
		try{
			parse(in, contentHandler, isResume);
		}
		catch(IOException ie){
			/*
			 * Actually it will never happen.
			 */
			throw new ParseException(-1, ParseException.ERROR_UNEXPECTED_EXCEPTION, ie);
		}
	}
	
	public void parse(Reader in, ContentHandler contentHandler) throws IOException, ParseException{
		parse(in, contentHandler, false);
	}
	
	/**
	 * Stream processing of JSON text.
	 * 
	 * @see ContentHandler
	 * 
	 * @param in
	 * @param contentHandler
	 * @param isResume - Indicates if it continues previous parsing operation.
     *                   If set to true, resume parsing the old stream, and parameter 'in' will be ignored. 
	 *                   If this method is called for the first time in this instance, isResume will be ignored.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public void parse(Reader in, ContentHandler contentHandler, boolean isResume) throws IOException, ParseException{
		if(isResume){
                    if(handlerStatusStack == null){
                        reset(in);
                        handlerStatusStack = new LinkedList();
                    }
                } else {
                    reset(in);
                    handlerStatusStack = new LinkedList();
                }
		
		LinkedList statusStack = handlerStatusStack;	
		
		try{
			do{
				switch(status){
				case S_INIT -> {
                                    contentHandler.startJSON();
                                    nextToken();
                                    switch(token.type){
                                        case Yytoken.TYPE_VALUE -> {
                                            status=S_IN_FINISHED_VALUE;
                                            statusStack.addFirst(status);
                                            if(!contentHandler.primitive(token.value))
                                                return;
                                        }
                                        case Yytoken.TYPE_LEFT_BRACE -> {
                                            status=S_IN_OBJECT;
                                            statusStack.addFirst(status);
                                            if(!contentHandler.startObject())
                                                return;
                                        }
                                        case Yytoken.TYPE_LEFT_SQUARE -> {
                                            status=S_IN_ARRAY;
                                            statusStack.addFirst(status);
                                            if(!contentHandler.startArray())
                                                return;
                                        }
                                        default -> status=S_IN_ERROR;
                                    }//inner switch
                                }
					
				case S_IN_FINISHED_VALUE -> {
                                    nextToken();
                                    if(token.type==Yytoken.TYPE_EOF){
                                        contentHandler.endJSON();
                                        status = S_END;
                                        return;
                                    }
                                    else{
                                        status = S_IN_ERROR;
                                        throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
                                    }
                                }
			
				case S_IN_OBJECT -> {
                                    nextToken();
                                    switch(token.type){
                                        case Yytoken.TYPE_COMMA -> {
                                        }
                                        case Yytoken.TYPE_VALUE -> {
                                            if(token.value instanceof String key){
                                                status=S_PASSED_PAIR_KEY;
                                                statusStack.addFirst(status);
                                                if(!contentHandler.startObjectEntry(key))
                                                    return;
                                            }
                                            else{
                                                status=S_IN_ERROR;
                                            }
                                        }

                                        case Yytoken.TYPE_RIGHT_BRACE -> {
                                            if(statusStack.size()>1){
                                                statusStack.removeFirst();
                                                status=peekStatus(statusStack);
                                            }
                                            else{
                                                status=S_IN_FINISHED_VALUE;
                                            }
                                            if(!contentHandler.endObject())
                                                return;
                                        }
                                        default -> status=S_IN_ERROR;
                                    }//inner switch
                                }
					
				case S_PASSED_PAIR_KEY -> {
                                    nextToken();
                                    switch(token.type){
                                        case Yytoken.TYPE_COLON -> {
                                        }
                                        case Yytoken.TYPE_VALUE -> {
                                            statusStack.removeFirst();
                                            status=peekStatus(statusStack);
                                            if(!contentHandler.primitive(token.value))
                                                return;
                                            if(!contentHandler.endObjectEntry())
                                                return;
                                        }
                                        case Yytoken.TYPE_LEFT_SQUARE -> {
                                            statusStack.removeFirst();
                                            statusStack.addFirst(S_IN_PAIR_VALUE);
                                            status=S_IN_ARRAY;
                                            statusStack.addFirst(status);
                                            if(!contentHandler.startArray())
                                                return;
                                        }
                                        case Yytoken.TYPE_LEFT_BRACE -> {
                                            statusStack.removeFirst();
                                            statusStack.addFirst(S_IN_PAIR_VALUE);
                                            status=S_IN_OBJECT;
                                            statusStack.addFirst(status);
                                            if(!contentHandler.startObject())
                                                return;
                                        }
                                        default -> status=S_IN_ERROR;
                                    }
                                }
				
				case S_IN_PAIR_VALUE -> {
                                    /*
                                    * S_IN_PAIR_VALUE is just a marker to indicate the end of an object entry, it doesn't proccess any token,
                                    * therefore delay consuming token until next round.
                                    */
                                    statusStack.removeFirst();
                                    status = peekStatus(statusStack);
                                    if(!contentHandler.endObjectEntry())
                                        return;
                                }
					
				case S_IN_ARRAY -> {
                                    nextToken();
                                    switch(token.type){
                                        case Yytoken.TYPE_COMMA -> {
                                        }
                                        case Yytoken.TYPE_VALUE -> {
                                            if(!contentHandler.primitive(token.value))
                                                return;
                                        }
                                        case Yytoken.TYPE_RIGHT_SQUARE -> {
                                            if(statusStack.size()>1){
                                                statusStack.removeFirst();
                                                status=peekStatus(statusStack);
                                            }
                                            else{
                                                status=S_IN_FINISHED_VALUE;
                                            }
                                            if(!contentHandler.endArray())
                                                return;
                                        }
                                        case Yytoken.TYPE_LEFT_BRACE -> {
                                            status=S_IN_OBJECT;
                                            statusStack.addFirst(status);
                                            if(!contentHandler.startObject())
                                                return;
                                        }
                                        case Yytoken.TYPE_LEFT_SQUARE -> {
                                            status=S_IN_ARRAY;
                                            statusStack.addFirst(status);
                                            if(!contentHandler.startArray())
                                                return;
                                        }
                                        default -> status=S_IN_ERROR;
                                    }//inner switch
                                }
					
				case S_END -> {
                                    return;
                                }
					
				case S_IN_ERROR -> throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
				}//switch
				if(status==S_IN_ERROR){
					throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
				}
			}while(token.type!=Yytoken.TYPE_EOF);
		}
		catch(IOException | ParseException | RuntimeException | Error ie){
			status = S_IN_ERROR;
			throw ie;
		}
		
		status = S_IN_ERROR;
		throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
	}
}