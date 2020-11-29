package com.springboot.finalproject.controllers;

import com.springboot.finalproject.entities.Countries;
import com.springboot.finalproject.entities.ShopCars;
import com.springboot.finalproject.entities.Users;
import com.springboot.finalproject.services.ShopCarService;
import com.springboot.finalproject.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.PushBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class ShopCarController {

    @Autowired
    private ShopCarService shopCarService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    Для профайла
    @Value("${file.baseUploadUrl}")
    private String baseUrl;

    @Value("${file.viewUploadUrl}")
    private String viewUrl;

    @Value("${avatar.defaultPic}")
    private String defaultAva;

//    Для индекса
    @Value("${file.imgBaseUploadUrl}")
    private String imgBaseUrl;

    @Value("${file.imgViewUploadUrl}")
    private String imgViewUrl;

    @Value("${avatar.defaultImg}")
    private String defaultImg;



    @GetMapping(value = "/")
    public String index(Model model, @RequestParam(name = "name", required = false, defaultValue = "") String name,
                        @RequestParam(name = "fromPrice", required = false) Double fromPrice,
                        @RequestParam(name = "toPrice", required = false) Double toPrice,
                        @RequestParam(name = "fromYear", required = false) Integer fromYear,
                        @RequestParam(name = "toYear", required = false) Integer toYear){

        model.addAttribute("currentUser", getUser());

        List<ShopCars> shopCars = shopCarService.searchCars(name, fromPrice, toPrice, fromYear, toYear);
        model.addAttribute("shopCars", shopCars);

        List<Countries> countries = shopCarService.getAllCountries();
        model.addAttribute("countries", countries);

        model.addAttribute("searchName", name!=null?name:"");
        model.addAttribute("searchPriceFrom", fromPrice!=null?fromPrice:"");
        model.addAttribute("searchPriceTo", toPrice!=null?toPrice:"");
        model.addAttribute("searchYearFrom", fromYear!=null?fromYear:"");
        model.addAttribute("searchYearTo", toYear!=null?toYear:"");

        return "index";
    }

    @GetMapping(value = "/search")
    public String search(Model model, @RequestParam(name = "name") String name){

        model.addAttribute("currentUser", getUser());

        List<ShopCars> shopCars = shopCarService.searchShopCars(name);
        model.addAttribute("shopCars", shopCars);
        return "index";
    }

    @PostMapping(value = "/addcar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addCar(@RequestParam(name = "name") String name,
                         @RequestParam(name = "description") String description,
                         @RequestParam(name = "price") double price,
                         @RequestParam(name = "tel") String tel,
                         @RequestParam(name = "year") int year,
                         @RequestParam(name = "country_id") Long countryId,
                         @RequestParam(name = "img") MultipartFile file){

        Countries country = shopCarService.getCountry(countryId);

        if (country!=null){
            ShopCars shopCar = new ShopCars();
            shopCar.setName(name);
            shopCar.setDescription(description);
            shopCar.setPrice(price);
            shopCar.setTel(tel);
            shopCar.setYear(year);
            shopCar.setCountry(country);

//         ******** Новая часть *********************
            Users currentUser = getUser();
            String imgName = DigestUtils.sha1Hex("img_"+currentUser.getId()+"_picture");

            if (file.getContentType().equals("image/jpeg")||file.getContentType().equals("image/png")){
                String ext = "png";

                if (file.getContentType().equals("image/jpeg")){
                    ext = "jpg";
                }

                imgName = imgName + "."+ext;
                try{

                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(imgBaseUrl+imgName);
                    Files.write(path, bytes);

                    shopCar.setImgUrl(imgName);
                    shopCarService.addShopCar(shopCar);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return "redirect:/additem";

//        ********Конец Новой части **********************
//            shopCarService.addShopCar(shopCar);
        }
        return "redirect:/";
    }

    @GetMapping(value = "/details/{idshka}")
    public String details(@PathVariable(name = "idshka") Long id, Model model){

        model.addAttribute("currentUser", getUser());

        ShopCars shopCar = shopCarService.getShopCar(id);
        if (shopCar!=null){
            model.addAttribute("shopCar", shopCar);

            return "details";
        }else{
            return "redirect:/";
        }
    }

    @PostMapping(value = "/savecar")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String saveCar(@RequestParam(name = "id") Long id,
                          @RequestParam(name = "name") String name,
                          @RequestParam(name = "description") String description,
                          @RequestParam(name = "price") double price,
                          @RequestParam(name = "tel") String tel,
                          @RequestParam(name = "year") int year,
                          @RequestParam(name = "country_id") Long countryId){

        ShopCars shopCar = shopCarService.getShopCar(id);

        if (shopCar!=null){
            Countries country = shopCarService.getCountry(countryId);

            if (country!=null){
                shopCar.setName(name);
                shopCar.setDescription(description);
                shopCar.setPrice(price);
                shopCar.setYear(year);
                shopCar.setTel(tel);
                shopCar.setCountry(country);

                shopCarService.saveShopCar(shopCar);
                return "redirect:/editcar/"+id;
            }

        }
        return "redirect:/";
    }

    @PostMapping(value = "/deletecar")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteCar(@RequestParam(name = "id") Long id){

        ShopCars shopCar = shopCarService.getShopCar(id);

        if (shopCar!=null){
            shopCarService.deleteShopCar(shopCar);
        }
        return "redirect:/";
    }

    @GetMapping(value = "/403")
    public String accessDeniedPage(Model model){
        model.addAttribute("currentUser", getUser());
        return "403";
    }

    @GetMapping(value = "/login")
    public String login(Model model){
        model.addAttribute("currentUser", getUser());
        return "login";
    }

    @GetMapping(value = "/register")
    public String register(Model model){
        model.addAttribute("currentUser", getUser());
        return "register";
    }

    @PostMapping(value = "register")
    public String toRegister(@RequestParam(name = "email") String email,
                             @RequestParam(name = "password") String password,
                             @RequestParam(name = "re_password") String rePassword,
                             @RequestParam(name = "full_name") String fullName){

        if (password.equals(rePassword)){

            Users newUser = new Users();
            newUser.setEmail(email);
            newUser.setFullName(fullName);
            newUser.setPassword(passwordEncoder.encode(password));

            if(userService.addUser(newUser)){
                return "redirect:/register?success";
            }else{
                return "redirect:/register?error";
            }
        }
        return "redirect:/register?passerror";
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model){

        model.addAttribute("currentUser", getUser());
        return "profile";
    }

    @GetMapping(value = "/additem")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addItem(Model model){

        model.addAttribute("currentUser", getUser());

        List<Countries> countries = shopCarService.getAllCountries();
        model.addAttribute("countries", countries);
        
        return "additem";
    }

    @GetMapping(value = "/editcar/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String editCar(@PathVariable(name = "id") Long id, Model model){

        model.addAttribute("currentUser", getUser());
        ShopCars shopCar = shopCarService.getShopCar(id);

        if (shopCar!=null){
            model.addAttribute("shopCar", shopCar);

            List<Countries> countries = shopCarService.getAllCountries();
            model.addAttribute("countries", countries);
            return "editcar";
        }else{
            return "redirect:/";
        }

    }

    @PostMapping(value = "/updateavatar")
    public String updateAvatar(@RequestParam(name = "avatar") MultipartFile file){

        Users currentUser = getUser();
        String picName = DigestUtils.sha1Hex("avatar_"+currentUser.getId()+"_picture");

        if (file.getContentType().equals("image/jpeg")||file.getContentType().equals("image/png")){
            String ext = "png";

            if (file.getContentType().equals("image/jpeg")){
                ext = "jpg";
            }

            picName = picName + "."+ext;
            try{

                byte[] bytes = file.getBytes();
                Path path = Paths.get(baseUrl+picName);
                Files.write(path, bytes);

                currentUser.setAvaUrl(picName);
                userService.updateUser(currentUser);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return "redirect:/profile";
    }

    @GetMapping(value = "/profilephoto/{url}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody byte[] profilePhoto(@PathVariable(name = "url") String url) throws IOException {

        String pictureUrl = viewUrl+defaultAva;

        if (url!=null){
            pictureUrl = viewUrl + url;
        }

        InputStream in;
        try{

            ClassPathResource resource = new ClassPathResource(pictureUrl);
            in = resource.getInputStream();

        }catch (Exception e){
            pictureUrl = viewUrl + defaultAva;
            ClassPathResource resource = new ClassPathResource(pictureUrl);
            in = resource.getInputStream();
        }
        return IOUtils.toByteArray(in);
    }

//    ********Для индекса***************

    @GetMapping(value = "/imgphoto/{url}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] imgPhoto(@PathVariable(name = "url") String url) throws IOException {

        String imgPictureUrl = imgViewUrl+defaultImg;

        if (url!=null){
            imgPictureUrl = imgViewUrl + url;
        }

        InputStream in;
        try{
            // если загрузят картинку, то будет показана
            ClassPathResource resource = new ClassPathResource(imgPictureUrl);
            in = resource.getInputStream();

        }catch (Exception e){
            // если нет загруженной картинки, то будет показывать default картинку
            imgPictureUrl = imgViewUrl + defaultImg;
            ClassPathResource resource = new ClassPathResource(imgPictureUrl);
            in = resource.getInputStream();
        }
        return IOUtils.toByteArray(in);
    }

//    нужно еще разобраться
    private Users getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)){

            User secUser=(User)authentication.getPrincipal();
            Users user = userService.getUserByEmail(secUser.getUsername());
            return user;
        }
        return null;
    }

}
