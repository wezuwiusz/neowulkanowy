# Wezuwiusz

Nieoficjalny klient dziennika VULCAN UONET+ dla ucznia i rodzica. Fork projektu Wulkanowy,
skupiony na rozwoju wsparcia dla mobilnego API. Celem projektu jest ukończenie wsparcia API mobilnego
na wystarczającym poziomie, aby domyślną opcją dla wszystkich użytkowników był tryb hybrydowy SDK
(wykorzystujemy API mobilne tam gdzie się ta, i jest wspierany scrapperem tam gdzie się nie da)

[Oficjalny kanał na telegramie](https://t.me/dzienniczekwezuwiusz)
[Oficjalny chat na telegramie](https://t.me/wezuwiuszchat)

## Funkcje

* logowanie za pomocą e-maila i hasła
* funkcje ze strony internetowej dziennika:
    * oceny
    * statystyki ocen
    * frekwencja
    * procent frekwencji
    * sprawdziany
    * plan lekcji
    * lekcje zrealizowane
    * wiadomości
    * zadania domowe
    * uwagi
    * szczęśliwy numerek
    * dodatkowe lekcje
    * zebrania w szkole
    * informacje o uczniu i szkole
* obliczanie średniej niezależnie od preferencji szkoły
* powiadomienia np. o nowej ocenie
* obsługa wielu kont wraz z możliwością zmiany nazwy ucznia 
* ciemny i czarny (AMOLED) motyw
* tryb offline

## Pobierz

Aktualnie projekt jest w fazie aktywnego rozwoju. Zalecam zbudowanie aplikacji ze źródeł.
SDK neoWulkanowego obecnie nie jest w żadnym repozytorium, zatem należy zbudować je osobno i podmienić
jar SDK w cache gradle.

## Zbudowana za pomocą

* [Wezuwiusz SDK](https://github.com/wezuwiusz/sdk)
* [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
* [Hilt](https://dagger.dev/hilt/)
* [Room](https://developer.android.com/topic/libraries/architecture/room)
* [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) 

## Współpraca

Wnieś swój wkład w projekt, tworząc PR lub wysyłając issue na GitHub.

## Licencja

Ten projekt udostępniany jest na licencji Apache License 2.0 - szczegóły w pliku [LICENSE](LICENSE)
