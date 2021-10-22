function Test-Validation {
    param (
       $baseURL,
       $platform,
       $log
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
        Write-Output "Test Validation $platform`n$time`n`nTest ISBN Creation`nPrefix: $randomPrefix`nRegistration Group: $randomRegGroup`nRegistrant: $randomRegistrant`nPublication: $randomPublic`nGenerated ISBN: $bodyGenerateISBN `nStatuscode: $statusGenerateIsbn`n" >> $log
    }
    else {
        Write-Output "Test Validation $platform`n$time`n`nTest ISBN Creation`nPrefix: $prefix`nRegistration Group: $regGroup`nRegistrant: $registrant`nPublication: $publication`nStatuscode: $statusGenerateIsbn`nResponse: $bodyGenerateISBN`n" >> $log
    }

    $bodyGenerateChecksum = Invoke-RestMethod -Method GET -Uri "$($generateCheckSumUrl)$($randomISBNwochecksum)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck
    if ($statusGenerateChecksum -eq 200){
        Write-Output "Test Checksum Validation`nISBN without Checksum: $randomISBNwochecksum`nChecksum: $bodyGenerateChecksum `nStatuscode: $statusGenerateIsbn`n" >> $log
    }
    else {
        Write-Output "Test Checksum Validation`nISBN without Checksum: $randomISBNwochecksum`nResponse: $bodyGenerateChecksum `nStatuscode: $statusGenerateIsbn`n" >> $log
    }

    $bodyValidateIsbn = Invoke-RestMethod -Method GET -Uri "$($validateUrl)$($randomISBN)" -StatusCodeVariable "statusValidateIsbn" -SkipHttpErrorCheck
    Write-Output "Test ISBN Validation`nISBN: $randomISBN`nResponse: $bodyValidateIsbn`nStatuscode: $statusValidateIsbn`n" >> $log
}

function Test-Backend {
    param (
        $baseURL,
        $platform,
        $log,
        $booklist
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
    Write-Output "Test Backend $platform`n$time`n`nTest Book Save`nTitel: $titel`nAutor: $autor`nVerlag: $verlag`nISBN: $isbn13`nResponse: $bodySavebook`nStatuscode: $statusBackendIsbn`n" >> $log

    $bodySavebook2 = Invoke-RestMethod -Method POST -Uri "$($savebookURL)" -body ($params2|ConvertTo-Json) -ContentType "application/json" -StatusCodeVariable "statusBackendIsbn" -SkipHttpErrorCheck
    Write-Output "Titel: $titel2`nAutor: $autor2`nVerlag: $verlag2`nISBN: $isbn132`nResponse: $bodySavebook2`nStatuscode: $statusBackendIsbn`n" >> $log

    $bodyReadbook = Invoke-RestMethod -Method GET -Uri "$($readbookURL)$($isbn13)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck
    Write-Output "Test Readbook`nID: $($bodyReadbook.id)`nTitel: $($bodyReadbook.titel)`nAutor: $($bodyReadbook.autor)`nVerlag: $($bodyReadbook.verlag)`nISBN: $($bodyReadbook.isbn13)`n" >> $log

    $bodyBook = Invoke-RestMethod -Method GET -Uri "$($booksURL)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck -OutFile .\Test.json -PassThru
    for ($i = 0; $i -lt $bodyBook.length; $i++) {
        if ($i -eq 0){
            Write-Output "Booklist`n" > $booklist
        }
        Write-Output "ID: $($bodyBook[$i].id)`nTitel: $($bodyBook[$i].titel)`nAutor: $($bodyBook[$i].autor)`nVerlag: $($bodyBook[$i].verlag)`nISBN: $($bodyBook[$i].isbn13)`n" >> $booklist
    }

}

#Startzeit des Skripts speichern
$time = Get-Date 

#Logfiles definieren
$functionLog = ".\FunctionTesting.log"
$booklist = ".\Booklist.log"
$stressLogValidationLocal = ".\StressTestingValidationLocal.log"
$stressLogBackendLocal = ".\StressTestingBackendLocal.log"
$stressLogValidationAzure = ".\StressTestingValidationAzure.log"
$stressLogBackendAzure = ".\StressTestingBackendAzure.log"

#Base URLs definieren
$baseUrlValidationLocal = "http://localhost:8081/isbn"
$baseUrlBackendLocal = "http://localhost:8080/book"
$baseUrlValidationAzure = "http://saic-isbn-validation-doc.azurewebsites.net/isbn"
$baseUrlBackendAzure = "http://saic-isbn-backend-doc.azurewebsites.net/book"

#Service Status abfragen und speichern
$statusValidationLocal = docker container inspect -f '{{.State.Status}}' "saic-app-isbn-validation-1"
$statusBackendLocal = docker container inspect -f '{{.State.Status}}' "saic-app-isbn-backend-1"
$statusValidationAzure = Invoke-RestMethod -Method GET -Uri "http://saic-isbn-validation-doc.azurewebsites.net/check/ping" -SkipHttpErrorCheck
$statusBackendAzure = Invoke-RestMethod -Method GET -Uri "http://saic-isbn-backend-doc.azurewebsites.net/check/ping" -SkipHttpErrorCheck

$choice = Read-Host "Test Local [L] or Azure [A]? [Default: L]"
if ($choice -eq "") {
    $choice = "l"
}
switch ($choice){
    {$_ -eq "Local" -or $_ -eq "l"} {
        
        $choice_local = Read-Host "Test Backend [E], Validation [V] or both [B]? [Default: B]"
        if ($choice_local -eq "") {
            $choice_local = "b"
        }

        switch ($choice_local) {

            {$_ -eq "Validation" -or $_ -eq "v"} {
                
                if ($statusValidationLocal -eq "running"){


                    $choice_functionlv = Read-Host "Run function test? [Y/N] [Default: Y]"

                    if ($choice_functionlv -eq "") {
                        $choice_functionlv = "y"
                    }

                    if ($choice_functionlv -eq "y" -or $choice_functionlv -eq "yes") {
                        Test-Validation -baseURL $baseUrlValidationLocal -platform "Docker" -log $functionLog
                    }

                    else {
                        Write-Output "$time`nFunction testing was not run." >> $log
                    }


                    $choice_stresslv = Read-Host "Run stress testing? (Y/N) [Default: Y]"

                    if ($choice_stresslv -eq "") {
                        $choice_stresslv = "y"
                    }

                    if($choice_stresslv -eq "y" -or $choice_stresslv -eq "yes") {

                        $runs = Read-Host "How many runs for stress test?"
                        $parallels = Read-Host "How many runs in parallel?"
                        
                        if ($runs -match '^\d+$' -and $parallels -match '^\d+$') {

                            $runs = [int]$runs
                            $parallels = [int]$parallels

                            if ($runs -ge 1 -and $parallels -ge 1) {
                                Write-Output "Stress testing Validation Docker`n$time`n" >> $stressLogValidationLocal
                                $jobs = 1..$runs | ForEach-Object -Parallel {
                                    function Test-ValidationStress {
                                        param (
                                            $baseURL
                                        )
                                        $randomPrefix = Get-Random -Minimum 978 -Maximum 979
                                        $randomRegGroup = Get-Random -Minimum 0 -Maximum 9
                                        $randomRegistrant = Get-Random -Minimum 10000 -Maximum 99999
                                        $randomPublic = Get-Random -Minimum 100 -Maximum 999
                                        $randomISBNwochecksum = Get-Random -Minimum 978000000000 -Maximum 979999999999
                                        $randomISBN = Get-Random -Minimum 9780000000000 -Maximum 9799999999999
                                    
                                        $validateUrl = "$($baseURL)/validate?isbn="
                                        $generateCheckSumUrl = "$($baseURL)/generateCheckSum?isbnWithoutChecksum="
                                        $generateIsbnUrl = "$($baseURL)/generateIsbn?prefix=$($randomPrefix)&registrationGroup=$($randomRegGroup)&registrant=$($randomRegistrant)&publicationElement=$($randomPublic)"
                                    
                                        Invoke-RestMethod -Method GET -Uri "$($generateIsbnUrl)" -StatusCodeVariable "statusGenerateIsbn" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($generateCheckSumUrl)$($randomISBNwochecksum)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($validateUrl)$($randomISBN)" -StatusCodeVariable "statusValidateIsbn" -SkipHttpErrorCheck > $null
                                    }

                                    Test-ValidationStress -baseURL $using:baseURLValidationLocal

                                    docker stats --format "{{.Name}}: {{.CPUPerc}} {{.MemUsage}}" --no-stream saic-app-isbn-validation-1 #>> C:\Users\renne\Desktop\Lasttest\Docker\Validation\Lasttest_Docker_Validation_run_$($_).log
                                    
                                } -ThrottleLimit $parallels -AsJob

                                $jobs | Receive-Job -Wait >> $stressLogValidationLocal
                                Write-Output "$runs runs completed, with $parallels running in parallel every time.`n" >> $stressLogValidationLocal
                                Write-Output "Stress testing successfully finished. See $($stressLogValidationLocal.substring(2)) for results.`n" >> $functionLog 
                            }

                            else {
                                Write-Output "$runs and/or $parallels is not a valid Number, stress testing aborted!`n" >> $log
                            }
                        
                        }
                        else {
                            Write-Output "$runs and/or $parallels is not a valid input, stress testing aborted!`n" >> $log
                        }
                        
                    }

                    else {
                        Write-Output "$time`nStress testing was not run." >> $log
                    }
        
                    exit
                }

                else{
                    "Error!`n$time`nNo running container for Validation found.`nPlease start required container.`n" >> $log
                    exit
                }
            }
            {$_ -eq "Backend" -or $_ -eq "e"} {

                if ($statusBackendLocal -eq "running"){

                    $choice_functionle = Read-Host "Run function test? [Y/N] [Default: Y]"

                    if ($choice_functionle -eq "") {
                        $choice_functionle = "y"
                    }

                    if ($choice_functionle -eq "y" -or $choice_functionle -eq "yes") {
                        Test-Backend -baseURL $baseUrlBackendLocal -platform "Docker" -log $functionLog
                    }

                    else {
                        Write-Output "$time`nFunction testing was not run." >> $log
                    }

                    $choice_stressle = Read-Host "Run stress testing for Backend Local? (Y/N) [Default: Y]"

                    if ($choice_stressle -eq "") {
                        $choice_stressle = "y"
                    }

                    if($choice_stressle -eq "y" -or $choice_stressle -eq "yes") {

                        $runs = Read-Host "How many runs for stress test?"
                        $parallels = Read-Host "How many runs in parallel?"
                        
                        if ($runs -match '^\d+$' -and $parallels -match '^\d+$') {

                            $runs = [int]$runs
                            $parallels = [int]$parallels

                            if ($runs -ge 1 -and $parallels -ge 1) {
                                Write-Output "Stress testing Backend Local`n$time`n" >> $stressLogBackendLocal
                                $jobs = 1..$runs | ForEach-Object -Parallel {
                                    function Test-BackendStress {
                                        param (
                                            $baseURL
                                        )

                                        $titel = "Test"
                                        $autor = "Test McTesty"
                                        $verlag = "Test Verlag"
                                        $isbn13 = Get-Random -Minimum 9780000000000 -Maximum 9799999999999

                                        $params = @{
                                            "titel"="$titel";
                                            "autor"="$autor";
                                            "verlag"="$verlag";
                                            "isbn13"="$isbn13"
                                        }

                                        $savebookURL = "$($baseURL)/saveBook"
                                        $readbookURL = "$($baseURL)/readBook?isbn="
                                        $booksURL = "$($baseURL)/books"

                                        Invoke-RestMethod -Method POST -Uri "$($savebookURL)" -body ($params|ConvertTo-Json) -ContentType "application/json" -StatusCodeVariable "statusBackendIsbn" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($readbookURL)$($isbn13)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($booksURL)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck -OutFile .\Test.json -PassThru > $null
                                    }

                                    Test-BackendStress -baseURL $using:baseURLBackendLocal

                                    docker stats --format "{{.Name}}: {{.CPUPerc}} {{.MemUsage}}" --no-stream saic-app-isbn-backend-1
                                    
                                } -ThrottleLimit $parallels -AsJob

                                $jobs | Receive-Job -Wait >> $stressLogBackendLocal
                                Write-Output "$runs runs completed, with $parallels running in parallel every time.`n" >> $stressLogBackendLocal
                                Write-Output "Stress testing successfully finished. See $($stressLogBackendLocal.substring(2)) for results.`n" >> $functionLog 
                            }

                            else {
                                Write-Output "$runs and/or $parallels is not a valid Number, stress testing aborted!`n" >> $log
                            }
                        
                        }
                        else {
                            Write-Output "$runs and/or $parallels is not a valid input, stress testing aborted!`n" >> $log
                        }
                        
                    }

                    else {
                        Write-Output "$time`nStress testing was not run." >> $log
                    }
                }

                else{
                    "$time`nNo running container for Backend found.`nPlease start required container.`n" >> $log
                    exit
                }
            }
            {$_ -eq "both" -or $_ -eq "b"} {

                if ($statusValidationLocal -eq "running" -and $statusBackendLocal -eq "running"){


                    $choice_functionlb = Read-Host "Run function testing? [Y/N] [Default: Y]"

                    if ($choice_functionlb -eq "") {
                        $choice_functionlb = "y"
                    }

                    if ($choice_functionlb -eq "y" -or $choice_functionlb -eq "yes") {
                        Test-Validation -baseURL $baseUrlValidationLocal -platform "Docker" -log $functionLog
                        Test-Backend -baseURL $baseUrlBackendLocal -platform "Docker" -log $functionLog
                    }

                    else {
                        Write-Output "$time`nFunction testing was not run." >> $log
                    }


                    $choice_stresslb = Read-Host "Run stress testing? (Y/N) [Default: Y]"

                    if ($choice_stresslb -eq "") {
                        $choice_stresslb = "y"
                    }

                    if($choice_stresslb -eq "y" -or $choice_stresslv -eq "yes") {

                        $runs = Read-Host "How many runs for stress test?"
                        $parallels = Read-Host "How many runs in parallel?"
                        
                        if ($runs -match '^\d+$' -and $parallels -match '^\d+$') {

                            $runs = [int]$runs
                            $parallels = [int]$parallels

                            if ($runs -ge 1 -and $parallels -ge 1) {
                                Write-Output "Stress testing Validation Docker`n$time`n" >> $stressLogValidationLocal
                                $jobsVL = 1..$runs | ForEach-Object -Parallel {
                                    function Test-ValidationStress {
                                        param (
                                            $baseURL
                                        )
                                        $randomPrefix = Get-Random -Minimum 978 -Maximum 979
                                        $randomRegGroup = Get-Random -Minimum 0 -Maximum 9
                                        $randomRegistrant = Get-Random -Minimum 10000 -Maximum 99999
                                        $randomPublic = Get-Random -Minimum 100 -Maximum 999
                                        $randomISBNwochecksum = Get-Random -Minimum 978000000000 -Maximum 979999999999
                                        $randomISBN = Get-Random -Minimum 9780000000000 -Maximum 9799999999999
                                    
                                        $validateUrl = "$($baseURL)/validate?isbn="
                                        $generateCheckSumUrl = "$($baseURL)/generateCheckSum?isbnWithoutChecksum="
                                        $generateIsbnUrl = "$($baseURL)/generateIsbn?prefix=$($randomPrefix)&registrationGroup=$($randomRegGroup)&registrant=$($randomRegistrant)&publicationElement=$($randomPublic)"
                                    
                                        Invoke-RestMethod -Method GET -Uri "$($generateIsbnUrl)" -StatusCodeVariable "statusGenerateIsbn" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($generateCheckSumUrl)$($randomISBNwochecksum)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($validateUrl)$($randomISBN)" -StatusCodeVariable "statusValidateIsbn" -SkipHttpErrorCheck > $null
                                    }

                                    Test-ValidationStress -baseURL $using:baseURLValidationLocal

                                    docker stats --format "{{.Name}}: {{.CPUPerc}} {{.MemUsage}}" --no-stream saic-app-isbn-validation-1 #>> C:\Users\renne\Desktop\Lasttest\Docker\Validation\Lasttest_Docker_Validation_run_$($_).log
                                    
                                } -ThrottleLimit $parallels -AsJob

                                $jobsVL | Receive-Job -Wait >> $stressLogValidationLocal
                                Write-Output "$runs runs completed, with $parallels running in parallel every time.`n" >> $stressLogValidationLocal

                                Write-Output "Stress testing Backend Local`n$time`n" >> $stressLogBackendLocal
                                $jobsBL = 1..$runs | ForEach-Object -Parallel {
                                    function Test-BackendStress {
                                        param (
                                            $baseURL
                                        )

                                        $titel = "Test"
                                        $autor = "Test McTesty"
                                        $verlag = "Test Verlag"
                                        $isbn13 = Get-Random -Minimum 9780000000000 -Maximum 9799999999999

                                        $params = @{
                                            "titel"="$titel";
                                            "autor"="$autor";
                                            "verlag"="$verlag";
                                            "isbn13"="$isbn13"
                                        }

                                        $savebookURL = "$($baseURL)/saveBook"
                                        $readbookURL = "$($baseURL)/readBook?isbn="
                                        $booksURL = "$($baseURL)/books"

                                        Invoke-RestMethod -Method POST -Uri "$($savebookURL)" -body ($params|ConvertTo-Json) -ContentType "application/json" -StatusCodeVariable "statusBackendIsbn" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($readbookURL)$($isbn13)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($booksURL)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck -OutFile .\Test.json -PassThru > $null
                                    }

                                    Test-BackendStress -baseURL $using:baseURLBackendLocal

                                    docker stats --format "{{.Name}}: {{.CPUPerc}} {{.MemUsage}}" --no-stream saic-app-isbn-backend-1
                                    
                                } -ThrottleLimit $parallels -AsJob

                                $jobsBL | Receive-Job -Wait >> $stressLogBackendLocal
                                Write-Output "$runs runs completed, with $parallels running in parallel every time.`n" >> $stressLogBackendLocal


                                Write-Output "Stress testing for Validation Service Local successfully finished. See $($stressLogValidationLocal.substring(2)) for results.`n" >> $functionLog 
                                Write-Output "Stress testing for Backend Service Local successfully finished. See $($stressLogBackendLocal.substring(2)) for results.`n" >> $functionLog
                            }

                            else {
                                Write-Output "$runs and/or $parallels is not a valid Number, stress testing aborted!`n" >> $log
                            }
                        
                        }
                        else {
                            Write-Output "$runs and/or $parallels is not a valid input, stress testing aborted!`n" >> $log
                        }
                        
                    }

                    else {
                        Write-Output "$time`nStress testing was not run." >> $log
                    }
        
                    exit
                }

                
                
            }
            Default {
                Write-Output "Error`n$time`nChoice $choice_local is not a valid input. Script was terminated.`n" >> $log
                exit
            }
        }
    }

    {$_ -eq "Azure" -or $_ -eq "a"} {

        $choice_azure = Read-Host "Test Backend [E], Validation [V] or both [B]? [Default: B]"
        if ($choice_azure -eq "") {
            $choice_azure = "b"
        }

        switch ($choice_azure) {

            {$_ -eq "Validation" -or $_ -eq "v"} {
                
                if ($statusValidationAzure -eq "pong"){


                    $choice_functionav = Read-Host "Run function test? [Y/N] [Default: Y]"

                    if ($choice_functionav -eq "") {
                        $choice_functionav = "y"
                    }

                    if ($choice_functionav -eq "y" -or $choice_functionav -eq "yes") {
                        Test-Validation -baseURL $baseUrlValidationAzure -platform "Azure" -log $functionLog
                    }

                    else {
                        Write-Output "$time`nFunction testing was not run." >> $log
                    }


                    $choice_stressav = Read-Host "Run stress testing? (Y/N) [Default: Y]"

                    if ($choice_stressav -eq "") {
                        $choice_stressav = "y"
                    }

                    if($choice_stressav -eq "y" -or $choice_stressav -eq "yes") {

                        $runs = Read-Host "How many runs for stress test?"
                        $parallels = Read-Host "How many runs in parallel?"
                        
                        if ($runs -match '^\d+$' -and $parallels -match '^\d+$') {

                            $runs = [int]$runs
                            $parallels = [int]$parallels

                            if ($runs -ge 1 -and $parallels -ge 1) {
                                Write-Output "Stress testing Validation Azure`n$time`n" >> $stressLogValidationAzure
                                $jobs = 1..$runs | ForEach-Object -Parallel {
                                    function Test-ValidationStress {
                                        param (
                                            $baseURL
                                        )
                                        $randomPrefix = Get-Random -Minimum 978 -Maximum 979
                                        $randomRegGroup = Get-Random -Minimum 0 -Maximum 9
                                        $randomRegistrant = Get-Random -Minimum 10000 -Maximum 99999
                                        $randomPublic = Get-Random -Minimum 100 -Maximum 999
                                        $randomISBNwochecksum = Get-Random -Minimum 978000000000 -Maximum 979999999999
                                        $randomISBN = Get-Random -Minimum 9780000000000 -Maximum 9799999999999
                                    
                                        $validateUrl = "$($baseURL)/validate?isbn="
                                        $generateCheckSumUrl = "$($baseURL)/generateCheckSum?isbnWithoutChecksum="
                                        $generateIsbnUrl = "$($baseURL)/generateIsbn?prefix=$($randomPrefix)&registrationGroup=$($randomRegGroup)&registrant=$($randomRegistrant)&publicationElement=$($randomPublic)"
                                    
                                        Invoke-RestMethod -Method GET -Uri "$($generateIsbnUrl)" -StatusCodeVariable "statusGenerateIsbn" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($generateCheckSumUrl)$($randomISBNwochecksum)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($validateUrl)$($randomISBN)" -StatusCodeVariable "statusValidateIsbn" -SkipHttpErrorCheck > $null
                                    }

                                    Test-ValidationStress -baseURL $using:baseURLValidationAzure

                                    #docker stats --format "{{.Name}}: {{.CPUPerc}} {{.MemUsage}}" --no-stream saic-app-isbn-validation-1 #>> C:\Users\renne\Desktop\Lasttest\Docker\Validation\Lasttest_Docker_Validation_run_$($_).log
                                    
                                } -ThrottleLimit $parallels -AsJob

                                $jobs | Receive-Job -Wait >> $stressLogValidationAzure
                                Write-Output "$runs runs completed, with $parallels running in parallel every time.`n" >> $stressLogValidationAzure
                                Write-Output "Stress testing successfully finished. See $($stressLogValidationAzure.substring(2)) for results.`n" >> $functionLog 
                            }

                            else {
                                Write-Output "$runs and/or $parallels is not a valid Number, stress testing aborted!`n" >> $log
                            }
                        
                        }
                        else {
                            Write-Output "$runs and/or $parallels is not a valid input, stress testing aborted!`n" >> $log
                        }
                        
                    }

                    else {
                        Write-Output "$time`nStress testing was not run." >> $log
                    }
        
                    exit
                }

                else{
                    "Error!`n$time`nNo running Web service for Validation found.`nPlease start required Web Service.`n" >> $log
                    exit
                }
            }
            {$_ -eq "Backend" -or $_ -eq "e"} {

                if ($statusBackendAzure -eq "pong"){

                    $choice_functionae = Read-Host "Run function test? [Y/N] [Default: Y]"

                    if ($choice_functionae -eq "") {
                        $choice_functionae = "y"
                    }

                    if ($choice_functionae -eq "y" -or $choice_functionae -eq "yes") {
                        Test-Backend -baseURL $baseUrlBackendAzure -platform "Azure" -log $functionLog
                    }

                    else {
                        Write-Output "$time`nFunction testing was not run." >> $log
                    }

                    $choice_stressae = Read-Host "Run stress testing for Backend Azure? (Y/N) [Default: Y]"

                    if ($choice_stressae -eq "") {
                        $choice_stressae = "y"
                    }

                    if($choice_stressae -eq "y" -or $choice_stressae -eq "yes") {

                        $runs = Read-Host "How many runs for stress test?"
                        $parallels = Read-Host "How many runs in parallel?"
                        
                        if ($runs -match '^\d+$' -and $parallels -match '^\d+$') {

                            $runs = [int]$runs
                            $parallels = [int]$parallels

                            if ($runs -ge 1 -and $parallels -ge 1) {
                                Write-Output "Stress testing Backend Azure`n$time`n" >> $stressLogBackendAzure
                                $jobs = 1..$runs | ForEach-Object -Parallel {
                                    function Test-BackendStress {
                                        param (
                                            $baseURL
                                        )

                                        $titel = "Test"
                                        $autor = "Test McTesty"
                                        $verlag = "Test Verlag"
                                        $isbn13 = Get-Random -Minimum 9780000000000 -Maximum 9799999999999

                                        $params = @{
                                            "titel"="$titel";
                                            "autor"="$autor";
                                            "verlag"="$verlag";
                                            "isbn13"="$isbn13"
                                        }

                                        $savebookURL = "$($baseURL)/saveBook"
                                        $readbookURL = "$($baseURL)/readBook?isbn="
                                        $booksURL = "$($baseURL)/books"

                                        Invoke-RestMethod -Method POST -Uri "$($savebookURL)" -body ($params|ConvertTo-Json) -ContentType "application/json" -StatusCodeVariable "statusBackendIsbn" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($readbookURL)$($isbn13)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($booksURL)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck -OutFile .\Test.json -PassThru > $null
                                    }

                                    Test-BackendStress -baseURL $using:baseURLBackendAzure

                                    #docker stats --format "{{.Name}}: {{.CPUPerc}} {{.MemUsage}}" --no-stream saic-app-isbn-backend-1
                                    
                                } -ThrottleLimit $parallels -AsJob

                                $jobs | Receive-Job -Wait >> $stressLogBackendAzure
                                Write-Output "$runs runs completed, with $parallels running in parallel every time.`n" >> $stressLogBackendLocal
                                Write-Output "Stress testing successfully finished. See $($stressLogBackendLocal.substring(2)) for results.`n" >> $functionLog 
                            }

                            else {
                                Write-Output "$runs and/or $parallels is not a valid Number, stress testing aborted!`n" >> $log
                            }
                        
                        }
                        else {
                            Write-Output "$runs and/or $parallels is not a valid input, stress testing aborted!`n" >> $log
                        }
                        
                    }

                    else {
                        Write-Output "$time`nStress testing was not run." >> $log
                    }
                }

                else{
                    "$time`nNo running Web service for Backend found.`nPlease start required Web service.`n" >> $log
                    exit
                }
            }
            {$_ -eq "both" -or $_ -eq "b"} {

                if ($statusValidationAzure -eq "pong" -and $statusBackendAzure -eq "pong"){


                    $choice_functionab = Read-Host "Run function testing? [Y/N] [Default: Y]"

                    if ($choice_functionab -eq "") {
                        $choice_functionab = "y"
                    }

                    if ($choice_functionab -eq "y" -or $choice_functionab -eq "yes") {
                        Test-Validation -baseURL $baseUrlValidationAzure -platform "Azure" -log $functionLog
                        Test-Backend -baseURL $baseUrlBackendAzure -platform "Azure" -log $functionLog
                    }

                    else {
                        Write-Output "$time`nFunction testing was not run." >> $log
                    }


                    $choice_stressab = Read-Host "Run stress testing? (Y/N) [Default: Y]"

                    if ($choice_stressab -eq "") {
                        $choice_stressab = "y"
                    }

                    if($choice_stressab -eq "y" -or $choice_stresslv -eq "yes") {

                        $runs = Read-Host "How many runs for stress test?"
                        $parallels = Read-Host "How many runs in parallel?"
                        
                        if ($runs -match '^\d+$' -and $parallels -match '^\d+$') {

                            $runs = [int]$runs
                            $parallels = [int]$parallels

                            if ($runs -ge 1 -and $parallels -ge 1) {
                                Write-Output "Stress testing Validation Azure`n$time`n" >> $stressLogValidationAzure
                                $jobsVA = 1..$runs | ForEach-Object -Parallel {
                                    function Test-ValidationStress {
                                        param (
                                            $baseURL
                                        )
                                        $randomPrefix = Get-Random -Minimum 978 -Maximum 979
                                        $randomRegGroup = Get-Random -Minimum 0 -Maximum 9
                                        $randomRegistrant = Get-Random -Minimum 10000 -Maximum 99999
                                        $randomPublic = Get-Random -Minimum 100 -Maximum 999
                                        $randomISBNwochecksum = Get-Random -Minimum 978000000000 -Maximum 979999999999
                                        $randomISBN = Get-Random -Minimum 9780000000000 -Maximum 9799999999999
                                    
                                        $validateUrl = "$($baseURL)/validate?isbn="
                                        $generateCheckSumUrl = "$($baseURL)/generateCheckSum?isbnWithoutChecksum="
                                        $generateIsbnUrl = "$($baseURL)/generateIsbn?prefix=$($randomPrefix)&registrationGroup=$($randomRegGroup)&registrant=$($randomRegistrant)&publicationElement=$($randomPublic)"
                                    
                                        Invoke-RestMethod -Method GET -Uri "$($generateIsbnUrl)" -StatusCodeVariable "statusGenerateIsbn" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($generateCheckSumUrl)$($randomISBNwochecksum)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($validateUrl)$($randomISBN)" -StatusCodeVariable "statusValidateIsbn" -SkipHttpErrorCheck > $null
                                    }

                                    Test-ValidationStress -baseURL $using:baseURLValidationAzure

                                    #docker stats --format "{{.Name}}: {{.CPUPerc}} {{.MemUsage}}" --no-stream saic-app-isbn-validation-1 #>> C:\Users\renne\Desktop\Lasttest\Docker\Validation\Lasttest_Docker_Validation_run_$($_).log
                                    
                                } -ThrottleLimit $parallels -AsJob

                                $jobsVA | Receive-Job -Wait >> $stressLogValidationAzure
                                Write-Output "$runs runs completed, with $parallels running in parallel every time.`n" >> $stressLogValidationAzure

                                Write-Output "Stress testing Backend Local`n$time`n" >> $stressLogBackendAzure
                                $jobsBA = 1..$runs | ForEach-Object -Parallel {
                                    function Test-BackendStress {
                                        param (
                                            $baseURL
                                        )

                                        $titel = "Test"
                                        $autor = "Test McTesty"
                                        $verlag = "Test Verlag"
                                        $isbn13 = Get-Random -Minimum 9780000000000 -Maximum 9799999999999

                                        $params = @{
                                            "titel"="$titel";
                                            "autor"="$autor";
                                            "verlag"="$verlag";
                                            "isbn13"="$isbn13"
                                        }

                                        $savebookURL = "$($baseURL)/saveBook"
                                        $readbookURL = "$($baseURL)/readBook?isbn="
                                        $booksURL = "$($baseURL)/books"

                                        Invoke-RestMethod -Method POST -Uri "$($savebookURL)" -body ($params|ConvertTo-Json) -ContentType "application/json" -StatusCodeVariable "statusBackendIsbn" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($readbookURL)$($isbn13)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck > $null
                                        Invoke-RestMethod -Method GET -Uri "$($booksURL)" -StatusCodeVariable "statusGenerateChecksum" -SkipHttpErrorCheck -OutFile .\Test.json -PassThru > $null
                                    }

                                    Test-BackendStress -baseURL $using:baseURLBackendAzure

                                    #docker stats --format "{{.Name}}: {{.CPUPerc}} {{.MemUsage}}" --no-stream saic-app-isbn-backend-1
                                    
                                } -ThrottleLimit $parallels -AsJob

                                $jobsBA | Receive-Job -Wait >> $stressLogBackendAzure
                                Write-Output "$runs runs completed, with $parallels running in parallel every time.`n" >> $stressLogBackendAzure


                                Write-Output "Stress testing for Validation Service Local successfully finished. See $($stressLogValidationLocal.substring(2)) for results.`n" >> $functionLog 
                                Write-Output "Stress testing for Backend Service Local successfully finished. See $($stressLogBackendLocal.substring(2)) for results.`n" >> $functionLog
                            }

                            else {
                                Write-Output "$runs and/or $parallels is not a valid Number, stress testing aborted!`n" >> $log
                            }
                        
                        }
                        else {
                            Write-Output "$runs and/or $parallels is not a valid input, stress testing aborted!`n" >> $log
                        }
                        
                    }

                    else {
                        Write-Output "$time`nStress testing was not run." >> $log
                    }
        
                    exit
                }

                
                
            }
            Default {
                Write-Output "Error`n$time`nChoice $choice_azure is not a valid input. Script was terminated.`n" >> $log
                exit
            }
        }
    }

    Default {
        Write-Output "Error`n$time`nChoice $choice is not a valid input. Script was terminated.`n" >> $log
        exit
    }
            
}