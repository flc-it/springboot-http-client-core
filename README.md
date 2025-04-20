# springboot-http-core-client

## Présentation
Le projet *springboot-http-core-client* est la librairie Core pour l'utilisation des clients HTTP.

## Frameworks
- [Spring boot](https://spring.io/projects/spring-boot) [@2.7.18](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html)

## Dependencies
- [FLC-IT SPB Commons Core lib](https://github.com/flc-it/springboot-commons-core)
- [Apache Http Client](https://hc.apache.org/httpcomponents-client-ga)

## Paramétrage des clients HTTP
- Url => url:string
- Activation => active:true|false (default is true)
- Timeout connection => connect-timeout:milliseconds
- Timeout request connection => connection-request-timeout:milliseconds
- Timeout socket => socket-timeout:milliseconds
- Certificat SSL verification => ssl-certificate-verification:true|false (default is true)
- Proxy => proxy:true|false (default is false)
- Streaming => streaming:true|false (default is false)
- Chunk size => chunk-size:bytes (default is 4096)
- Simple client [SimpleClientHttp : HttpURLConnection](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/client/SimpleClientHttpRequestFactory.html) => simple-client:true|false (default is false)
- Connection par Route (Apache client) => max-connection-per-route:int (default is 2)
- Connection total (Apache client) => max-connection-total:int (default is 20)

### Load balancing
base => *.load-balancer.
- Urls => urls:string[]
- Activation => active:true|false (default is false)
- Algorithm => algorithm:roundRobin|weightRoundRobin|ipHash|random|weightRandom (default is RoundRobin)
- Weights => weights:int[] (only for weightRoundRobin & weightRandom, default is null)

Important :  
Mettre une seule url dans la liste des urls désactive automatiquement et complètement le load balancer (fail-over inclus).

### Fail over
base => *.load-balancer.fail-over.
- Activation => active:true|false (default is false)
- Fail Max attempt => fail-max-attempt:int (default is 3, first included)
- Retry Max attempt => retry-max-attempt:int (default is 2, first included)
- Timeout => tiemout:long (default is 60000)
- Fail status => fail-status:int[] (503 is automatic)
- Retry status => retry-status:int[]

## Valeurs par défaut
base => http.client.core.connection.default.
- Timeout connection => connect-timeout:milliseconds (default is undefined)
- Timeout request connection => connection-request-timeout:milliseconds (default is undefined)
- Timeout socket => socket-timeout:milliseconds (default is undefined)
- Proxy => proxy:true|false (default is undefined)
- Streaming => streaming:true|false (default is undefined)
- Chunk size => chunk-size:bytes (default is undefined)
- Simple client [SimpleClientHttp : HttpURLConnection](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/client/SimpleClientHttpRequestFactory.html) => simple-client:true|false (default is undefined)
- Connection par Route (Apache client) => max-connection-per-route:int (default is 15)
- Connection total (Apache client) => max-connection-total:int (default is 60)

## Paramétrage du proxy
base => proxy.
- Hostname => hosname:string
- Port => port:int (default is scheme default port)
- Domain => domain:string
- Username => username:string
- Password => password:string

## Exemples de client HTTP

### Client normal avec des timeout
```properties
flc-it.service.url=https://services.flc-it.fr/api
flc-it.service.connect-timeout=5000
flc-it.service.connection-request-timeout=10000
flc-it.service.socket-timeout=5000
```

### Client normal avec des max connections
```properties
flc-it.service.url=https://services.flc-it.fr/api
flc-it.service.max-connection-per-route=10
flc-it.service.max-connection-total=50
```

### Client basic avec proxy
```properties
flc-it.service.url=https://services.flc-it.fr/api
flc-it.service.proxy=true
flc-it.service.simple-client-http=true

# PROXY
proxy.hostname=flc-proxy.flc.dm.ad
proxy.port=8080
proxy.domain=FLC
proxy.username=${ldap.compte-service.login}
proxy.password=${ldap.compte-service.password}
```

### Client basic avec streaming
```properties
flc-it.service.url=https://services.flc-it.fr/api
flc-it.service.streaming=true
flc-it.service.simple-client-http=true
```

### Client avec load balancing et fail over
```properties
bpm.service.load-balancer.urls=http://flc-it1.flc.dm.ad:8080/bpm,http://flc-it2.flc.dm.ad:8080/bpm
bpm.service.load-balancer.active=true
bpm.service.load-balancer.fail-over.active=true
```

### Client avec load balancing et fail over désactivé
```properties
bpm.service.load-balancer.urls=https://flc-it-prod.flc.dm.ad/bpm
bpm.service.load-balancer.active=true
bpm.service.load-balancer.fail-over.active=true
```