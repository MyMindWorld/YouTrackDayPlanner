# DayPlanner

Полезное приложение для составления планов на день и отчётов

Приложение доступно по адресу [http://192.168.72.45:8520/DayPlanner/](http://192.168.72.45:8520/DayPlanner/)

Собирается и деплоится джобой [DayPlannerPipeline](https://jenkins.protei.ru/view/all/job/qa/job/DayPlanner/job/DayPlannerPipeline/)

### Составление плана
1. Выберите фильтр по задачам для отображения в списке
    По умолчанию следующий: `Состояние: New,Discuss,Active,Review,Test,Paused,Open`
    
    К каждому вашему фильтру будет добавлено условие `(Исполнитель: ${login} или Рецензент: {login})`
2. Выберите задачу из выпадающего списка и оцените сколько времени вы на неё планируете сегодня потратить
    Для удобства рядом с добавлением в план есть кнопка открывающая эту задачу в новой вкладке
3. После того как вы выбрали время, нажимайте `Add to plan`
4. Под выпадающим списком есть поле в которое можно написать пункт в плане на который ещё нет задачи или добавить митап `Add meeting`

5. После заполнения плана нажмите `Copy to clipboard` и [отправьте его в чат](https://mattermost.protei.ru/qa/channels/qa_ui) 

### Составление отчёта

Для составления отчёта необходимо переключиться на вкладку `Reporter` в верхнем левом углу

В поле слева можно добавить план и нажать `Process`, после чего приложение соберёт ваше затраченное за дату плана время и составит отчёт,
 который в свою очередь можно скопировать в буфер обмена
 
 
 ### Known Issues
 
 0. Сбор затраченного времени собирается по задачам обновлённым в день плана (Парсится из `План на dd.MM.yyyy:`)
    Однако если это было скажем вчера, а задача была обновлена после 12 - такая задача в план не попадёт. 
    Не сложно исправить, но не очень актуально так как план в 99% случаев составляется на месте.
 1. Не все задачи попадают в выпадающий список. Нужно [подправить запрос](./src/main/java/ru/protei/dayPlanner/Utils/YoutrackRequests.java)
  или [механизм сбора задач](./src/main/java/ru/protei/dayPlanner/controller/ReporterRestController.java)