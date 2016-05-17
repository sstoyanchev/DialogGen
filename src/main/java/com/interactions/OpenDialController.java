package com.interactions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class OpenDialController {

    @RequestMapping("/jumbotron")
    public String jumbotron(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "examples/jumbotron/index";
    }
    
    //TODO: replace with HTTPRequst
    @RequestMapping(value="/genDialog", method = RequestMethod.POST)
    @ResponseBody 
    public ResponseEntity<FileSystemResource> generateDialogSubmit(@ModelAttribute OpenDialFormXML f , Model model) throws Exception {
        System.out.println("Generating dialog for field names " + f.toString());
        String outFname = "openDialXml/test.xml";
        System.out.println("In genDialog Conroller SS");
        Resource resource = new ClassPathResource("xml/formFillingTemplate.xml");
        f.fillTemplate(resource.getInputStream(), outFname);
        
        //serialize the form
        //Gson gson = new Gson();
        //String json = gson.toJson(f);
        //System.out.println("json: " + json);

        System.out.println("Created file " + outFname);
        File file = new File ("openDialXml/test.xml") ;
        return ResponseEntity.ok()
        		.header("Content-Disposition", "attachment; filename=opendial.xml")
        		.body(new FileSystemResource(file)); 
    }
    
    @RequestMapping(value="/downloadOpendial", method = RequestMethod.GET)
    @ResponseBody 
    public ResponseEntity<FileSystemResource> downloadOpendial() throws Exception {

        File file = new File ("openDialXml/opendialFormFilling.tar.gz") ;
        return ResponseEntity.ok()
        		.header("Content-Disposition", "attachment; filename=opendialFormFilling.tar.gz")
        		.body(new FileSystemResource(file)); 
    }
    
//    @ExceptionHandler
//    public ResponseEntity<String> handleException(IOException ex) {
//    	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("oops");
//    }
    
    @RequestMapping(path = "/showFile", method = RequestMethod.GET, produces=MediaType.APPLICATION_XML_VALUE)
    @ResponseBody 
    public ResponseEntity<FileSystemResource> showFile() {
        File file = new File ("openDialXml/test.xml") ;
        return ResponseEntity.ok().body(new FileSystemResource(file)); 
    }     

    @RequestMapping("/opendialform")
    public String opendialform() {        
        return "opendialform";
    } 
    
    @RequestMapping("/demo-eval")
    public String demo() {        
        return "greeting";
    }     
    
    @RequestMapping(path="/opendialformWatson", method=RequestMethod.GET)
    public String opendialformExampleWatson(Model model) {  
    	//model.addAttribute("formObject", new OpenDialFormXML("opendialWatson.json"));
    	model.addAttribute("formJson", "{test}");
        return "opendialform";
    }
}

