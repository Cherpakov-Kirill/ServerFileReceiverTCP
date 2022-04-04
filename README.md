# ServerFileReceiverTCP
File receiver. Sever

# Передача файла по TCP с подсчётом скорости передачи данных
Вам необходимо разработать протокол передачи произвольного файла с одного компьютера на другой, и написать клиент и сервер, реализующие этот протокол. Сервер также должен выводить скорость приёма данных от клиента.
1. Серверу передаётся в параметрах номер порта, на котором он будет ждать входящих соединений от клиентов.
2. Клиенту передаётся в параметрах относительный или абсолютный путь к файлу, который нужно отправить. Длина имени файла не превышает 4096 байт в кодировке UTF-8. Размер файла не более 1 терабайта.
3. Клиенту также передаётся в параметрах DNS-имя (или IP-адрес) и номер порта сервера.
4. Клиент передаёт серверу имя файла в кодировке UTF-8, размер файла и его содержимое. Для передачи используется TCP. Протокол передачи придумайте сами (т.е. программы разных студентов могут оказаться несовместимы).
5. Сервер сохраняет полученный файл в поддиректорию uploads своей текущей директории. Имя файла, по возможности, совпадает с именем, которое передал клиент. Сервер никогда не должен писать за пределы директории uploads.
6. В процессе приёма данных от клиента, сервер должен раз в 3 секунды выводить в консоль мгновенную скорость приёма и среднюю скорость за сеанс. Скорости выводятся отдельно для каждого активного клиента. Если клиент был активен менее 3 секунд, скорость всё равно должна быть выведена для него один раз. Под скоростью здесь подразумевается количество байтов переданных за единицу времени.
7. После успешного сохранения всего файла сервер проверяет, совпадает ли размер полученных данных с размером, переданным клиентом, и сообщает клиенту об успехе или неуспехе операции, после чего закрывает соединение.
8. Клиент должен вывести на экран сообщение о том, успешной ли была передача файла.
9. Все используемые ресурсы ОС должны быть корректно освобождены, как только они больше не нужны.
10. Сервер должен уметь работать параллельно с несколькими клиентами. Для этого необходимо использовать треды (POSIX threads или их аналог в вашей ОС). Сразу после приёма соединения от одного клиента, сервер ожидает следующих клиентов.
11. В случае ошибки сервер должен разорвать соединение с клиентом. При этом он должен продолжить обслуживать остальных клиентов.
