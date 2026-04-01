# documentação bruta 

anotações ao longo da construção do projeto, para não esquecer de nada

foi implementado 

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(role.toGrantedAuthority());
}
```

sabendo que retorna um role único, justamente por se tratar de um projeto simples, apesar de limitar a flexibilidade, sabemos que o sistema não crescerá indefinidamente com a necessidade de agregar mais roles 

outra observação de estudo foi a implementação da interface UserDetails diretamente pela entidade User, isso é algo que pode ser visto como uma má prática, pois mistura as responsabilidades de persistência e segurança, gerando acoplamento direto entre a entidade e o framework

