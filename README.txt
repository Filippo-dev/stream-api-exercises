- stream dentro un filtro per usare any match che ritorna boo
- @With per convertire
- flatmap target: figlio.stream
- limit
- peek ti fa usare un consumer e andare avanti con lo stream
- usa distinct!
- collectors.tomap -> produce mappe
- collectors.mapping -> dentro ai group by per selezionare solo alcuni campi degli ogg ragruppati
- collectors.maxby -> dentro ai group by per selezionare il massimo fra oggetti raggruppati
- collectors.counting -> dentro ai group by per beccare il numero di occorrenze
- collectors.summingInt -> dentro ai group by per fare le somme di liste interi

- values() invocabile quando ho ottenuto una mappa per streamare sulle values
- entrySet() invocabile quando ho ottenuto una mappa per streamare su key e values

- Pattern.compile("\n").splitAsStream(string) -> splitta stringa in streams
- concat -> concatena streams
- skip -> salta elementi di uno stream
- toArray -> converte streams in array

- .flatMap(employee -> Stream.of(employee.getHomeAddress(), employee.getCorrespondenceAddress())) -> crea un unica lista con i campi assieme