package my.code.effectivemobiletest.controllers;

import lombok.RequiredArgsConstructor;
import my.code.effectivemobiletest.dtos.CreateUserDto;
import my.code.effectivemobiletest.dtos.UserDto;
import my.code.effectivemobiletest.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserDto createUserDto){
        try {
            return new ResponseEntity<>(userService.create(createUserDto), HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/addPhoneNumber")
    public ResponseEntity<String> addPhoneNumber(@RequestParam String phoneNumberToAdd){
        try {
            return new ResponseEntity<>(userService.addPhoneNumber(phoneNumberToAdd), HttpStatus.OK);
        } catch (IllegalArgumentException | NullPointerException exceptions){
            return new ResponseEntity<>(exceptions.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
