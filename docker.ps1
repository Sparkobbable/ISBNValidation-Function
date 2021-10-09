& docker build --rm --tag isbn:latest .
& docker run -p 8080:80 -d --name ISBN -it isbn:latest
Write-Output " _____ ___________ _   _   _____           _     "
Write-Output "|_   _/  ___| ___ \ \ | | |_   _|         | |    "
Write-Output "  | | \ ``--.| |_/ /  \| |   | | ___   ___ | |___ "
Write-Output "  | |  ``--. \ ___ \ . `` |   | |/ _ \ / _ \| / __|"
Write-Output " _| |_/\__/ / |_/ / |\  |   | | (_) | (_) | \__ \"
Write-Output " \___/\____/\____/\_| \_/   \_/\___/ \___/|_|___/"