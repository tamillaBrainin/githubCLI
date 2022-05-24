package github;

import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Base64;
/**
 * @author Crunchify.com
 *
 */
 
@SuppressWarnings("deprecation")
public class CrunchifyReadGithubWithBasicAuthentication {
 
	public static void main(String[] args) {
 
		// Replace this token with your actual token
		String token = "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";
		getMenu(args,token);
		String url = "api.github.com/repos/whitesource/log4j-detect-distribution/releases";
		String url1 ="api.github.com/repos/whitesource/log4j-detect-distribution";
		String url2 ="api.github.com/repos/whitesource/log4j-detect-distribution/contributors";		
	}
	
 
	private static void getMenu(String[] args, String token) {
		final HelpFormatter formatter = new HelpFormatter();
		final String syntax = "This tool is used to get some stats from Github for specific repo\r\n"
				+ "This tool present the result as a table and write the output for a given\r\n"
				+ "file or just print it";
		final String usageHeader = "githubCLI [command]\n"+"Available Commands:";
		final String flags = "Flags:";
		Options options = new Options();
		Option completion = new Option("completion",false, "Generate the autocompletion script for the specified shell");
		Option downloads = new Option("downloads",true, "Present the available downloads for this repo");
//		Option downloadsO = new Option("downloads -o",true, "Present the available downloads for this repo and writes in file.Name must be provided");
		Option help = new Option("help",false, "Help about any command");
		Option stats = new Option("stats",true, "Present stats of repo from github");
		options.addOption(completion);		
		options.addOption(downloads);	
		options.addOption(help);
		options.addOption(stats);
		formatter.printHelp(syntax, usageHeader, options,flags);
		Options optionsFlags = new Options();
		Option h = new Option("h",false,"--help help for githubCLI");
		Option outputString = new Option("o",false, "--output string The path to output file");
		Option repoString = new Option("r",false, "--repo string  The repo to be read");
		optionsFlags.addOption(h);
		optionsFlags.addOption(outputString);
		optionsFlags.addOption(repoString);
		formatter.printHelp(flags,optionsFlags);	
		 CommandLine cmd = null;
	     CommandLineParser parser = new BasicParser();
	     HelpFormatter helper = new HelpFormatter();
	     String opt_config = null;
	     
		try {
            try {
				cmd = parser.parse(options, args);
			} catch (org.apache.commons.cli.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if(cmd.hasOption("completion")) {
                System.out.println("Completion activated");
                
            }          
            if (cmd.hasOption("downloads")) {
                opt_config = cmd.getOptionValue("downloads");
                System.out.println("Config set to " + opt_config);
                String url = "api.github.com/repos/"+opt_config;
                getGithubContentUsingHttpClient(token, url);
            }
            if (cmd.hasOption("stats")) {
            	opt_config = cmd.getOptionValue("stats");
                String url = "api.github.com/repos/"+opt_config+"/contributors";
                Long contributors  = getContributors(token,url);
                String url1 = "api.github.com/repos/"+opt_config;
                getStarsForkContent(token,url1,contributors);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helper.printHelp("Usage:", options);
            System.exit(0);
        }
	
	}
	
	
	
	private static Long getContributors(String token, String url2) {
		String newUrl = "https://" + token + ":x-oauth-basic@" + url2;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(newUrl);
		request.addHeader("content-type", "application/json");
		HttpResponse response = null;
		JSONParser parser = new JSONParser();  
		Long contributions = 0L;
		try {
			response = client.execute(request);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String responseString = new BasicResponseHandler().handleResponse((org.apache.http.HttpResponse) response);
			JSONArray json = (JSONArray) parser.parse(responseString);
			JSONObject jsonObject=(JSONObject) json.get(0);
			contributions=(Long) jsonObject.get("contributions");
		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contributions;
		
	}


	@SuppressWarnings("resource")
	private static void getGithubContentUsingHttpClient(String token, String url1) {
		String newUrl = "https://" + token + ":x-oauth-basic@" + url1;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(newUrl);
		request.addHeader("content-type", "application/json");		

		try {
			HttpResponse response = client.execute(request);
			String responseString = new BasicResponseHandler().handleResponse((org.apache.http.HttpResponse) response);			
			ObjectMapper mapper = new ObjectMapper();
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(responseString)));
//			Files.write(Paths.get("C:/develop/output/output.txt"),
//					mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(mapper.readTree(responseString)));
			JSONParser parser = new JSONParser();  
			try {
				
				JSONArray json = (JSONArray) parser.parse(responseString);
				JSONObject jsonObject=(JSONObject) json.get(0);
				//System.out.println(jsonObject.toString());
				JSONArray assets= (JSONArray) jsonObject.get("assets");
				Object releaseName=  jsonObject.get("name");
				
				System.out.println(assets.toString());
				String leftAlignFormat = "| %-20s    | %-20s    |%-4s |%n";

				System.out.format("+------------------------+------------------------------------------+-----------------+%n");
				System.out.format("| RELEASE NAME           | DISTRIBUTION                             |  DOWNLOAD COUNT  %n");
				System.out.format("+------------------------+------------------------------------------+-----------------+%n");				
				Long totalCount = 0L;
				for(int i=0;i<assets.size();i++) {
 					JSONObject object=(JSONObject) assets.get(i);
	                String name=(String) object.get("name");
	                Long download_count=(Long) object.get("download_count");
	                totalCount+=download_count;
	                System.out.format(leftAlignFormat, releaseName.toString() , name , download_count );
				}
				System.out.format("+-----------------------+---------------------------+------------------%n");
				System.out.println("Total Count  " + totalCount);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	private static void getStarsForkContent(String token, String url1, Long contributors) {
		String newUrl = "https://" + token + ":x-oauth-basic@" + url1;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(newUrl);
		request.addHeader("Content-type", "application/json");
		try {
			HttpResponse response = client.execute(request);
			String responseString = new BasicResponseHandler().handleResponse((org.apache.http.HttpResponse) response);
			JSONParser parser = new JSONParser();  
			try {
				
				JSONObject json = (JSONObject) parser.parse(responseString);
				Long stargazers_count=(Long) json.get("stargazers_count");
				Long forks_count=(Long) json.get("forks_count");
				String language = (String) json.get("language"); 
				String leftAlignFormat = "| %-23s | %-8s |%n";

				System.out.format("+-------------------------+--------+%n");
				System.out.format("| STAT                    | VALUE  |%n");
				System.out.format("+-------------------------+--------+%n");
				
				System.out.format(leftAlignFormat, "Stars" , stargazers_count);
				System.out.format(leftAlignFormat, "Forks" , forks_count);
				System.out.format(leftAlignFormat, "Contributors" , contributors);
				System.out.format(leftAlignFormat, "Language" , language);
				System.out.format("+-------------------------+--------+%n");
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
 
 

