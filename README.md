# KAM
KAM is a **K**otlin **A**uto **M**apper library for simplify basic mappers for your project via reflection and building extension-mappers of your model

## Current features
Now library support only `OneToOneMapperSource` annotation, and returns result into console

### Sample
Source code:
```kotlin
@OneToOneMapperSource(
    toClass = Model2::class
)
data class Model(
    val id: String
)

data class Model2(
    val id: String
)
```

Outputs:
```kotlin
public fun Model.mapToModel2(): Model2 = Model2(
	id = id
)
```

## A little to do list
- [x] Large code refactor
- [ ] Annotation for renaming variable
- [ ] Analyse target variable type
- [ ] Support `OneToOneMapperTarget`
- [ ] Support type converters
- [ ] Add `IgnoreOnMapping` annotation
- [ ] Add files output
