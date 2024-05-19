package my.code.effectivemobiletest.controllers;

import lombok.RequiredArgsConstructor;
import my.code.effectivemobiletest.dtos.CreateUserDto;
import my.code.effectivemobiletest.dtos.UserDto;
import my.code.effectivemobiletest.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserDto createUserDto) {
        try {
            return new ResponseEntity<>(userService.create(createUserDto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/addPhoneNumber")
    public ResponseEntity<String> addPhoneNumber(@RequestParam String phoneNumberToAdd) {
        try {
            return new ResponseEntity<>(userService.addPhoneNumber(phoneNumberToAdd), HttpStatus.OK);
        } catch (IllegalArgumentException | NullPointerException exceptions) {
            return new ResponseEntity<>(exceptions.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/changePhoneNumber")
    public ResponseEntity<String> changePhoneNumber(@RequestParam String numberForChange,
                                                    @RequestParam String newNumber) {
        try {
            return new ResponseEntity<>(userService.changePhoneNumber(numberForChange, newNumber), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/addEmail")
    public ResponseEntity<String> addEmail(String emailToAdd) {
        try {
            return new ResponseEntity<>(userService.addEmail(emailToAdd), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/changeEmail")
    public ResponseEntity<String> changeEmail(@RequestParam String emailForChange,
                                              @RequestParam String newEmail) {
        try {
            return new ResponseEntity<>(userService.changeEmail(emailForChange, newEmail), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deletePhoneNumber")
    public ResponseEntity<String> deletePhoneNumber(@RequestParam String numberToDelete) {
        try {
            return new ResponseEntity<>(userService.deletePhoneNumber(numberToDelete), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteEmail")
    public ResponseEntity<String> deleteEmail(@RequestParam String emailToDelete) {
        try {
            return new ResponseEntity<>(userService.deleteEmail(emailToDelete), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/findByFilters")
    public ResponseEntity<List<UserDto>> findByFilters(@RequestParam(required = false) Date dateOfBirth,
                                                       @RequestParam(required = false) String phoneNumber,
                                                       @RequestParam(required = false) String fullName,
                                                       @RequestParam(required = false) String email) {
        try {
            return new ResponseEntity<>(userService.findByFilters(dateOfBirth,
                                                                  phoneNumber,
                                                                  fullName,
                                                                  email), HttpStatus.OK);
        } catch (RuntimeException runtimeException){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

