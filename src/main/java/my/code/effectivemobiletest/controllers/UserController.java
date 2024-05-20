package my.code.effectivemobiletest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import my.code.effectivemobiletest.dtos.CreateUserDto;
import my.code.effectivemobiletest.dtos.UserDto;
import my.code.effectivemobiletest.exceptions.InsufficientFundsException;
import my.code.effectivemobiletest.exceptions.SameUserTransactionException;
import my.code.effectivemobiletest.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь создан успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateUserDto.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Пользователь не был добавлен в базу")
    })
    @Operation(summary = "Энд поинт создает нового пользователя в системе")

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserDto createUserDto) {
        try {
            return new ResponseEntity<>(userService.create(createUserDto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Номер добавлен успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Номер занят")
    })
    @Operation(summary = "Энд поинт принимает номер телефона который нужно добавить пользователю," +
            " которого находит через токен")
    @PutMapping("/addPhoneNumber")
    public ResponseEntity<String> addPhoneNumber(@RequestParam String phoneNumberToAdd) {
        try {
            return new ResponseEntity<>(userService.addPhoneNumber(phoneNumberToAdd), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Номер заменен успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Номер не может быть заменен")
    })
    @Operation(summary = "Энд поинт принимает номер телефона который нужно поменять," +
            " и новый номер на его замену")
    @PutMapping("/changePhoneNumber")
    public ResponseEntity<String> changePhoneNumber(@RequestParam String numberForChange,
                                                    @RequestParam String newNumber) {
        try {
            return new ResponseEntity<>(userService.changePhoneNumber(numberForChange, newNumber), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Емайл добавлен успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Емайл занят")
    })
    @Operation(summary = "Энд поинт принимает емайл который нужно добавить пользователю," +
            " которого находит через токен")
    @PutMapping("/addEmail")
    public ResponseEntity<String> addEmail(String emailToAdd) {
        try {
            return new ResponseEntity<>(userService.addEmail(emailToAdd), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Емайл заменен успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Емайл не может быть заменен")
    })
    @Operation(summary = "Энд поинт принимает емайл который нужно поменять," +
            " и новый емайл на его замену")
    @PutMapping("/changeEmail")
    public ResponseEntity<String> changeEmail(@RequestParam String emailForChange,
                                              @RequestParam String newEmail) {
        try {
            return new ResponseEntity<>(userService.changeEmail(emailForChange, newEmail), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Номер удален успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Номер не может быть удален")
    })
    @Operation(summary = "Энд поинт принимает номер телефона который нужно удалить")
    @DeleteMapping("/deletePhoneNumber")
    public ResponseEntity<String> deletePhoneNumber(@RequestParam String numberToDelete) {
        try {
            return new ResponseEntity<>(userService.deletePhoneNumber(numberToDelete), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Емайл удален успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Емайл не может быть удален")
    })
    @Operation(summary = "Энд поинт принимает емайл который нужно удалить")
    @DeleteMapping("/deleteEmail")
    public ResponseEntity<String> deleteEmail(@RequestParam String emailToDelete) {
        try {
            return new ResponseEntity<>(userService.deleteEmail(emailToDelete), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователи найдены",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователей нет")
    })
    @Operation(summary = "Энд поинт делает поиск по переданным фильтрам")
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
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Транзакция прошла успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Транзакция не прошла проверки")
    })
    @Operation(summary = "Энд поинт принимает айди пользователя которому будут переведены средства и" +
            " сумму перевода")
    @PutMapping("/transaction")
    public ResponseEntity<String> transaction(@RequestParam Long transferToUserId,
                                              @RequestParam BigDecimal amountToTransfer) {
        try {
            return new ResponseEntity<>(userService.transaction(transferToUserId, amountToTransfer), HttpStatus.OK);
        } catch (NullPointerException | InsufficientFundsException |
                 SameUserTransactionException | IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

