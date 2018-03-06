# Datastructures Documentation

### StreetMap

A StreetMap represents a map of streets as an undirected graph. Since all simulation depends on the map and therefore such a StreetMap, this somewhat is the heart of it all. Therefore it is *extremely* important that its contents are semantically consistent. E.g., if a road is added, we need to give this information to the connected intersections as well. Even more complicated and prone to errors are removals of roads or intersections. All affected connections of other intersections/roads need to be adjusted. Additionally, the removal of a road or intersection entails the shifting of indices above the removed index. We therefore have to adjust all indeces in all roads and intersections.

These steps are prone to errors, which is why I already implemented them handily in the StreetMap class. Please use ONLY the provided methods. They were written for a reason . I also make attributes private for a reason. Don't just add a Getter/Setter to go around the privacy. This will fuck up the semantics heavily. Look through the available methods and see how you can work with them. If there really is no way, we implement a new one.

### Intersections and Roads

In general 