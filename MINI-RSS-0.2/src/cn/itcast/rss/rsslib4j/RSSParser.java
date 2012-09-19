//$Id: RSSParser.java,v 1.9 2004/03/28 13:07:16 taganaka Exp $
package cn.itcast.rss.rsslib4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * RSS Parser.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */


//1.拿到输入流  
//1)通过url  2)通过File  3)直接给你输入流  4)通过uri
//2.拿到编码方式
//2.解析  需要编码方式  输入流  和handler
public class RSSParser {

	private SAXParserFactory factory = RSSFactory.getInstance();
	private DefaultHandler hnd;
	private File f;
	private URL u;
	private InputStream in;
	private boolean validate;

	public RSSParser() {
		validate = false;
	}

	/**
	 * Set the event handler
	 * 
	 * @param h
	 *            the DefaultHandler
	 * 
	 */
	public void setHandler(DefaultHandler h) {
		hnd = h;
	}

	/**
	 * Set rss resource by local file name
	 * 
	 * @param file_name
	 *            loca file name
	 * @throws RSSException
	 */
	public void setXmlResource(String file_name) throws RSSException {
		f = new File(file_name);
		try {
			in = new FileInputStream(f);
		} catch (Exception e) {
			throw new RSSException("RSSParser::setXmlResource fails: "
					+ e.getMessage());
		}

	}

	
	
	/**
	 * Set rss resource by URL
	 * 
	 * @param ur the remote url
	 * @throws RSSException
	 */
	public void setXmlResource(URL ur) throws RSSException {
		u = ur;
		File ft = null;
		try {
			URLConnection con = u.openConnection();
			in = u.openStream();
		} catch (IOException e) {
			throw new RSSException("RSSParser::setXmlResource fails: "
					+ e.getMessage());
		}
	}

	/**
	 * set true if parse have to validate the document defoult is false
	 * 
	 * @param b  true or false
	 * @uml.property name="validate"
	 */
	public void setValidate(boolean b) {
		validate = b;
	}

	/**
	 * Parse rss file
	 * 
	 * @param filename
	 *            local file name
	 * @param handler
	 *            the handler
	 * @param validating
	 *            validate document??
	 * @throws RSSException
	 */
	public static void parseXmlFile(String filename, DefaultHandler handler,
			boolean validating) throws RSSException {
		RSSParser p = new RSSParser();
		p.setXmlResource(filename);
		p.setHandler(handler);
		p.setValidate(validating);
		p.parse();
	}

	/**
	 * Parse rss file from a url
	 * 
	 * @param remote_url remote rss file
	 * @param handler  the handler
	 * @param validating validate document??
	 * @throws RSSException
	 */
	public static void parseXmlFile(URL remote_url, DefaultHandler handler,
			boolean validating) throws RSSException {
		RSSParser p = new RSSParser();
		p.setXmlResource(remote_url);
		p.setHandler(handler);
		p.setValidate(validating);
		p.parse();
	}

	/**
	 * Parse rss file from a url whitch is provide by handler
	 * 
	 * @param remote_url remote rss file
	 * @param handler  the handler
	 * @param validating validate document??
	 * @throws RSSException
	 */
	public static void parseXmlFile( UrlRSSHandler handler,boolean validating) throws RSSException {
		RSSParser p = new RSSParser();
		p.setXmlResource(handler.getUrl());
		p.setHandler(handler);
		p.setValidate(validating);
		p.parse(); 	}
	
	/**
	 * Parse rss file from a url whitch is provide by handler
	 * 
	 * @param remote_url remote rss file
	 * @param handler  the handler
	 * @param validating validate document??
	 * @throws RSSException
	 */
	public static void parseXmlInStream(File file,DefaultHandler handler,boolean validating) throws RSSException {
		RSSParser p = new RSSParser();
		p.parse(file, handler, validating);
	}
	
	

	public static void parseXmlInStream(InputStream inStream,
			RecommChannelHandler handler, boolean validating) throws RSSException{
		RSSParser p = new RSSParser();
		p.parse(inStream, handler, validating);
	}

	private void parse(File file, DefaultHandler handler,boolean validating)throws RSSException {
		InputStream inStream=null;
		InputStream inStream2=null;
		try {
			inStream = new FileInputStream(file);
			factory.setValidating(validate);
			String encoding = this.detectorXmlCharset(inStream);
			inStream2 = new FileInputStream(file);
			InputSource inSource = new InputSource(new InputStreamReader(inStream2, encoding));
			factory.newSAXParser().parse(inSource, handler);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		} catch (IOException e) {
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		}finally{
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void parse(InputStream inStream, DefaultHandler handler,boolean validating)throws RSSException {
		String encoding="utf-8";
		try {
			factory.setValidating(validate);
			InputSource inSource = new InputSource(new InputStreamReader(inStream, encoding));
			factory.newSAXParser().parse(inSource, handler);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		} catch (IOException e) {
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		}finally{
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Try to fix null length bug
	 * 
	 * @throws IOException
	 * @throws RSSException
	 */
	private void fixZeroLength() throws IOException, RSSException {

		File ft = File.createTempFile(".rsslib4jbugfix", ".tmp");
		ft.deleteOnExit();
		FileWriter fw = new FileWriter(ft);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		BufferedWriter out = new BufferedWriter(fw);
		String line = "";
		while ((line = reader.readLine()) != null) {
			out.write(line + "\n");
		}
		out.flush();
		out.close();
		reader.close();
		fw.close();
		setXmlResource(ft.getAbsolutePath());
	}

	/**
	 * Call it at the end of the work to preserve memory
	 */
	public void free() {
		this.factory = null;
		this.f = null;
		this.in = null;
		this.hnd = null;
		System.gc();
	}

	/**
	 * Parse the document
	 * 
	 * @throws RSSException
	 */
	public void parse() throws RSSException {
		try {
			factory.setValidating(validate);
			String encoding = this.detectorXmlCharset(in);
			InputSource inSource = new InputSource(new InputStreamReader(u.openConnection().getInputStream(), encoding));
			factory.newSAXParser().parse(inSource, hnd);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		} catch (IOException e) {
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		}
	}
	
	/**
	 * 判断当前xml的编码方式。
	 * @param inStream
	 * @return
	 */
	public String detectorXmlCharset(InputStream inStream) {
		String charSetStr = null;
		try {
			InputStreamReader inReader = new InputStreamReader(inStream);
			BufferedReader bReader = new BufferedReader(inReader);
			String temp = bReader.readLine();
			if (temp.indexOf("utf-8") >= 0||temp.indexOf("UTF-8") >= 0) {
				charSetStr = "utf-8";
			} else if (temp.indexOf("gb2312") >= 0||temp.indexOf("GB2312") >= 0) {
				charSetStr = "gb2312";
			} else if (temp.indexOf("gbk") >= 0||temp.indexOf("GBK") >= 0) {
				charSetStr = "gbk";
			} else {
				// TODO 抛出编码字符不支持的异常
			}
		} catch (Exception e) {
			e.printStackTrace();// TODO 这里的异常处理，暂时先打印堆栈。
		}
		return charSetStr;
	}
	
	public void parse(UrlRSSHandler handler){
		URL url=handler.getUrl();
	}

}