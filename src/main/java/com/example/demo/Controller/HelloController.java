package com.example.demo.Controller;

import com.example.demo.Model.Hello;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HelloController {

    @RequestMapping(path = "hello", method = RequestMethod.GET)
    public String helloName(@RequestParam(required = false) String name, Model model) {
        if (name != null) {
            String splittedName = SplitByCapitalLetter(name);
            Hello hello = new Hello();
            hello.setName(splittedName);
            model.addAttribute("nameProduct", hello);
            return "hello_name";
        } else {
            return "hello";
        }
    }
    @RequestMapping(path = "author", method = RequestMethod.GET)
    public String author(){
        return "author";
    }

    private String SplitByCapitalLetter(String name){
        String new_string = "";
        for (int i=0; i < name.length(); i++){
            char c = name.charAt(i);
            if(Character.isUpperCase(c)){
                new_string = new_string + " " + c;
            }
            else {
                new_string = new_string + c;
            }
        }
        return new_string;
    }
}
