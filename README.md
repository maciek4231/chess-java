# PAP2024Z-Z23 - Aplikacja desktopowa Szachy

## Członkowie

-   Anna Tamelo - testy
-   Maciej Dyduch - serwer
-   Mateusz Nawrocki - aplikacja desktopowa

## Instalacja

Po sklonowaniu repozytorium, z jego korzenia:

-   aby zainstalować tylko klienta (żeby połączyć się z serwerem zdalnie): `sh ./tools/install_client_only.sh`
-   aby zainstalować klienta _oraz_ serwer: `sh ./tools/install_server_and_client.sh` - skrypt wymaga uprawnień superużytkownika, ponieważ instaluje mysql-server, oraz tworzy na nim bazę chess_db i konto admin

## Uruchomienie

Z korzenia repozytorium:

-   aby uruchomić klienta `sh ./tools/run_client.sh`
-   aby uruchomić serwer `sh ./tools/run_server.sh`

## Konfiguracja

Można zmienić adres, z którym łączy się klient - służy do tego plik konfiguracyjny `config.json`.
