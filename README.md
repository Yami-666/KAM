# KAM
KAM is a **K**otlin **A**uto **M**apper library for simplify basic mappers for your project via reflection and building extension-mappers of your model

## Current features
Now library support only `OneToOneSourceMapper` annotation, and returns result into console

### Sample
Source code:
```kotlin
@OneToOneSourceMapper(
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
- [ ] Large code refactor
- [ ] Support `OneToOneTargetMapper`
- [ ] Support type converters
- [ ] Add `IgnoreOnMapping` annotation
