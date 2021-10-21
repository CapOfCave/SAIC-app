﻿function Test-Validation {
    param (
       $baseURL,
       $platform
    )
    $time = Get-Date
    $randomPrefix = Get-Random -Minimum 978 -Maximum 979
    $randomRegGroup = Get-Random -Minimum 0 -Maximum 9
    $randomRegistrant = Get-Random -Minimum 10000 -Maximum 99999
    $randomPublic = Get-Random -Minimum 100 -Maximum 999
    $randomISBNwochecksum = Get-Random -Minimum 978000000000 -Maximum 979999999999
    $randomISBN = Get-Random -Minimum 9780000000000 -Maximum 9799999999999

    $validateUrl = "$($baseURL)/validate?isbn="
    $generateCheckSumUrl = "$($baseURL)/generateCheckSum?isbnWithoutChecksum="
    $generateIsbnUrl = "$($baseURL)/generateIsbn?prefix=$($randomPrefix)&registrationGroup=$($randomRegGroup)&registrant=$($randomRegistrant)&publicationElement=$($randomPublic)"

    $bodyGenerateISBN = Invoke-RestMethod -Method GET -Uri "$($generateIsbnUrl)" -StatusCodeVariable "statusGenerateIsbn" -SkipHttpErrorCheck
    if ($statusGenerateIsbn -eq 200){
        Write-Output "Test Validation $platform`n$time`n`nTest ISBN Creation`nPrefix: $randomPrefix`nRegistration Group: $randomRegGroup`nRegistrant: $randomRegistrant`nPublication: $randomPublic`nGenerated ISBN: $bodyGenerateISBN `nStatuscode: $statusGenerateIsbn`n" >> .\Test.log
    }
    else {
        Write-Output "Test Validation $platform`n$time`n`nTest ISBN Creation`nPrefix: $prefix`nRegistration Group: $regGroup`nRegistrant: $registrant`nPublication: $publication`nStatuscode: $statusGenerateIsbn`nResponse: $bodyGenerateISBN`n" >> .\Test.log
    }

    $bodyGenerateChecksum = Invoke-RestMethod -Method GET -Uri "$($generateCheckSumUrl)$($randomISBNwochecksum)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck
    if ($statusGenerateChecksum -eq 200){
        Write-Output "Test Checksum Validation`nISBN without Checksum: $randomISBNwochecksum`nChecksum: $bodyGenerateChecksum `nStatuscode: $statusGenerateIsbn`n" >> .\Test.log
    }
    else {
        Write-Output "Test Checksum Validation`nISBN without Checksum: $randomISBNwochecksum`nResponse: $bodyGenerateChecksum `nStatuscode: $statusGenerateIsbn`n" >> .\Test.log
    }

    $bodyValidateIsbn = Invoke-RestMethod -Method GET -Uri "$($validateUrl)$($randomISBN)" -StatusCodeVariable "statusValidateIsbn" -SkipHttpErrorCheck
    Write-Output "Test ISBN Validation`nISBN: $randomISBN`nResponse: $bodyValidateIsbn`nStatuscode: $statusValidateIsbn`n" >> .\Test.log
}

function Test-Backend {
    param (
        $baseURL,
        $platform
    )
    $time = Get-Date
    $titel = "Test"
    $autor = "Test McTesty"
    $verlag = "Test Verlag"
    $isbn13 = "9781234567897" #Get-Random -Minimum 9780000000000 -Maximum 9799999999999

    $titel2 = "Test 2: Electric Boogaloo"
    $autor2 = "Test McTesty"
    $verlag2 = "Test Verlag"
    $isbn132 = "9783125171541" #Get-Random -Minimum 9780000000000 -Maximum 9799999999999

    $params = @{
        "titel"="$titel";
        "autor"="$autor";
        "verlag"="$verlag";
        "isbn13"="$isbn13"
    }

    $params2 = @{
        "titel"="$titel2";
        "autor"="$autor2";
        "verlag"="$verlag2";
        "isbn13"="$isbn132"
    }

    $savebookURL = "$($baseURL)/saveBook"
    $readbookURL = "$($baseURL)/readBook?isbn="
    $booksURL = "$($baseURL)/books"

    $bodySavebook = Invoke-RestMethod -Method POST -Uri "$($savebookURL)" -body ($params|ConvertTo-Json) -ContentType "application/json" -StatusCodeVariable "statusBackendIsbn" -SkipHttpErrorCheck
    Write-Output "Test Backend $platform`n$time`n`nTest Book Save`nTitel: $titel`nAutor: $autor`nVerlag: $verlag`nISBN: $isbn13`nResponse: $bodySavebook`nStatuscode: $statusBackendIsbn`n" >> .\Test.log

    $bodySavebook2 = Invoke-RestMethod -Method POST -Uri "$($savebookURL)" -body ($params2|ConvertTo-Json) -ContentType "application/json" -StatusCodeVariable "statusBackendIsbn" -SkipHttpErrorCheck
    Write-Output "Titel: $titel2`nAutor: $autor2`nVerlag: $verlag2`nISBN: $isbn132`nResponse: $bodySavebook2`nStatuscode: $statusBackendIsbn`n" >> .\Test.log

    $bodyReadbook = Invoke-RestMethod -Method GET -Uri "$($readbookURL)$($isbn13)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck
    Write-Output "Test Readbook`nID: $($bodyReadbook.id)`nTitel: $($bodyReadbook.titel)`nAutor: $($bodyReadbook.autor)`nVerlag: $($bodyReadbook.verlag)`nISBN: $($bodyReadbook.isbn13)`n" >> .\Test.log

    $bodyBook = Invoke-RestMethod -Method GET -Uri "$($booksURL)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck -OutFile .\Test.json -PassThru
    for ($i = 0; $i -lt $bodyBook.length; $i++) {
        if ($i -eq 0){
            Write-Output "Booklist`n" > .\Booklist.log
        }
        Write-Output "ID: $($bodyBook[$i].id)`nTitel: $($bodyBook[$i].titel)`nAutor: $($bodyBook[$i].autor)`nVerlag: $($bodyBook[$i].verlag)`nISBN: $($bodyBook[$i].isbn13)`n" >> .\Booklist.log
    }

}



$time = Get-Date
$choice = Read-Host "Test Local [L], Azure [A] or both [B]?"
switch ($choice){
    {$_ -eq "Local" -or $_ -eq "l"} {
        $container_backend = "saic-app-isbn-backend-1"
        $container_validation = "saic-app-isbn-validation-1"

        $choice_local = Read-Host "Test Backend [E], Validation [V] or both [B]?"

        switch ($choice_local) {

            {$_ -eq "Validation" -or $_ -eq "v"} {
                $status_validation = docker container inspect -f '{{.State.Status}}' $container_validation
                if ($status_validation -eq "running"){

                    $baseUrlValidationLocal = "http://localhost:8081/isbn"
                    Test-Validation -baseURL $baseUrlValidationLocal -platform "Docker"
        
                    exit
                }

                else{
                    "$time`nNo running container for Validation found.`nPlease start required container.`n" >> .\Test.log
                    exit
                }
            }
            {$_ -eq "Backend" -or $_ -eq "e"} {
                $status_backend = docker container inspect -f '{{.State.Status}}' $container_backend
                if ($status_backend -eq "running"){

                    $baseUrlBackendLocal = "http://localhost:8080/book"
                    Test-Backend -baseURL $baseUrlBackendLocal -platform "Docker"
                            
                    exit
                }

                else{
                    "$time`nNo running container for Backend found.`nPlease start required container.`n" >> .\Test.log
                    exit
                }
            }
            {$_ -eq "both" -or $_ -eq "b"} {
                $status_validation = docker container inspect -f '{{.State.Status}}' $container_validation
                $status_backend = docker container inspect -f '{{.State.Status}}' $container_backend

                if ($status_validation -eq "running" -and $status_backend -eq "running") {
                    
                    $baseUrlValidationLocal = "http://localhost:8081/isbn"
                    Test-Validation -baseURL $baseUrlValidationLocal -platform "Docker"

                    $baseUrlBackendLocal = "http://localhost:8080/book"
                    Test-Backend -baseURL $baseUrlBackendLocal -platform "Docker"

                    exit
                }

                else {
                    "$time`nAt least one container is not running`nPlease start required containers`n" >> .\Test.log
                    exit
                }
                
            }
            Default {
                Write-Output "Choice not found. Terminating Script." >> .\Test.log
                exit
            }
        }
    }

    {$_ -eq "Azure" -or $_ -eq "a"} {

        $choice_azure = Read-Host "Test Backend [E], Validation [V] or both [B]?"

        switch ($choice_azure) {

            {$_ -eq "Validation" -or $_ -eq "v"} {
              
                $baseUrlValidationAzure = "https://saic-isbn-validation.azurewebsites.net/isbn"
                Test-Validation -baseURL $baseUrlValidationAzure -platform "Azure"
                exit

            }
            {$_ -eq "Backend" -or $_ -eq "e"} {
 
                $baseUrlBackendAzure = "https://saic-isbnbackend.azurewebsites.net/book"
                Test-Backend -baseURL $baseUrlBackendAzure -platform "Azure"                            
                exit                              
            }
            {$_ -eq "both" -or $_ -eq "b"} {
                
                $baseUrlValidationAzure = "https://saic-isbn-validation.azurewebsites.net/isbn"
                Test-Validation -baseURL $baseUrlValidationAzure -platform "Azure"

                $baseUrlBackendAzure = "https://saic-isbnbackend.azurewebsites.net/book"
                Test-Backend -baseURL $baseUrlBackendAzure -platform "Azure"

                exit
                
            }

            Default {
                Write-Output "Choice not found. Terminating Script." >> .\Test.log
                exit
            }
        }
        #"URLS: saic-validation.azurewebsites.net:80
        #saic-isbnbackend.azuewebsites.net:80"
    }

    {$_ -eq "Both" -or $_ -eq "b"} {
        $container_backend = "saic-app-isbn-backend-1"
        $container_validation = "saic-app-isbn-validation-1"

        $status_validation = docker container inspect -f '{{.State.Status}}' $container_validation
        $status_backend = docker container inspect -f '{{.State.Status}}' $container_backend

        if ($status_validation -eq "running" -and $status_backend -eq "running") {
                    
            $baseUrlValidationLocal = "http://localhost:8081/isbn"
            Test-Validation -baseURL $baseUrlValidationLocal -platform "Docker"

            $baseUrlBackendLocal = "http://localhost:8080/book"
            Test-Backend -baseURL $baseUrlBackendLocal -platform "Docker"

        }

        else {
            "$time`nAt least one container is not running`nPlease start required containers`n" >> .\Test.log
            
        }

        $baseUrlValidationAzure = "https://saic-isbn-validation.azurewebsites.net/isbn"
        Test-Validation -baseURL $baseUrlValidationAzure -platform "Azure"

        $baseUrlBackendAzure = "https://saic-isbnbackend.azurewebsites.net/book"
        Test-Backend -baseURL $baseUrlBackendAzure -platform "Azure"

        exit
    }

    Default {
        Write-Output "Error`n$time`nChoice not found. Script was terminated.`n" >> .\Test.log
        exit
    }
            
}