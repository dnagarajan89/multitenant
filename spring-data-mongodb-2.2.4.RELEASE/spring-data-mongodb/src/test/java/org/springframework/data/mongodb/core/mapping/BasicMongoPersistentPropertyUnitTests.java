/*
 * Copyright 2011-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mongodb.core.mapping;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.PropertyNameFieldNamingStrategy;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.util.ReflectionUtils;

/**
 * Unit test for {@link BasicMongoPersistentProperty}.
 *
 * @author Oliver Gierke
 * @author Christoph Strobl
 * @author Mark Paluch
 */
public class BasicMongoPersistentPropertyUnitTests {

	MongoPersistentEntity<Person> entity;

	@Before
	public void setup() {
		entity = new BasicMongoPersistentEntity<Person>(ClassTypeInformation.from(Person.class));
	}

	@Test
	public void usesAnnotatedFieldName() {

		Field field = ReflectionUtils.findField(Person.class, "firstname");
		assertThat(getPropertyFor(field).getFieldName()).isEqualTo("foo");
	}

	@Test
	public void returns_IdForIdProperty() {
		Field field = ReflectionUtils.findField(Person.class, "id");
		MongoPersistentProperty property = getPropertyFor(field);
		assertThat(property.isIdProperty()).isTrue();
		assertThat(property.getFieldName()).isEqualTo("_id");
	}

	@Test
	public void returnsPropertyNameForUnannotatedProperties() {

		Field field = ReflectionUtils.findField(Person.class, "lastname");
		assertThat(getPropertyFor(field).getFieldName()).isEqualTo("lastname");
	}

	@Test
	public void preventsNegativeOrder() {
		getPropertyFor(ReflectionUtils.findField(Person.class, "ssn"));
	}

	@Test // DATAMONGO-553
	public void usesPropertyAccessForThrowableCause() {

		BasicMongoPersistentEntity<Throwable> entity = new BasicMongoPersistentEntity<>(
				ClassTypeInformation.from(Throwable.class));
		MongoPersistentProperty property = getPropertyFor(entity, "cause");

		assertThat(property.usePropertyAccess()).isTrue();
	}

	@Test // DATAMONGO-607
	public void usesCustomFieldNamingStrategyByDefault() throws Exception {

		ClassTypeInformation<Person> type = ClassTypeInformation.from(Person.class);
		Field field = ReflectionUtils.findField(Person.class, "lastname");

		MongoPersistentProperty property = new BasicMongoPersistentProperty(Property.of(type, field), entity,
				SimpleTypeHolder.DEFAULT, UppercaseFieldNamingStrategy.INSTANCE);
		assertThat(property.getFieldName()).isEqualTo("LASTNAME");

		field = ReflectionUtils.findField(Person.class, "firstname");

		property = new BasicMongoPersistentProperty(Property.of(type, field), entity, SimpleTypeHolder.DEFAULT,
				UppercaseFieldNamingStrategy.INSTANCE);
		assertThat(property.getFieldName()).isEqualTo("foo");
	}

	@Test // DATAMONGO-607
	public void rejectsInvalidValueReturnedByFieldNamingStrategy() {

		ClassTypeInformation<Person> type = ClassTypeInformation.from(Person.class);
		Field field = ReflectionUtils.findField(Person.class, "lastname");

		MongoPersistentProperty property = new BasicMongoPersistentProperty(Property.of(type, field), entity,
				SimpleTypeHolder.DEFAULT, InvalidFieldNamingStrategy.INSTANCE);

		assertThatExceptionOfType(MappingException.class).isThrownBy(property::getFieldName)
				.withMessageContaining(InvalidFieldNamingStrategy.class.getName()).withMessageContaining(property.toString());
	}

	@Test // DATAMONGO-937
	public void shouldDetectAnnotatedLanguagePropertyCorrectly() {

		MongoPersistentProperty property = getPropertyFor(DocumentWithLanguageProperty.class, "lang");
		assertThat(property.isLanguageProperty()).isTrue();
	}

	@Test // DATAMONGO-937
	public void shouldDetectImplicitLanguagePropertyCorrectly() {

		MongoPersistentProperty property = getPropertyFor(DocumentWithImplicitLanguageProperty.class, "language");
		assertThat(property.isLanguageProperty()).isTrue();
	}

	@Test // DATAMONGO-976
	public void shouldDetectTextScorePropertyCorrectly() {

		MongoPersistentProperty property = getPropertyFor(DocumentWithTextScoreProperty.class, "score");
		assertThat(property.isTextScoreProperty()).isTrue();
	}

	@Test // DATAMONGO-976
	public void shouldDetectTextScoreAsReadOnlyProperty() {

		MongoPersistentProperty property = getPropertyFor(DocumentWithTextScoreProperty.class, "score");
		assertThat(property.isWritable()).isFalse();
	}

	@Test // DATAMONGO-1050
	public void shouldNotConsiderExplicitlyNameFieldAsIdProperty() {

		MongoPersistentProperty property = getPropertyFor(DocumentWithExplicitlyRenamedIdProperty.class, "id");
		assertThat(property.isIdProperty()).isFalse();
	}

	@Test // DATAMONGO-1050
	public void shouldConsiderPropertyAsIdWhenExplicitlyAnnotatedWithIdEvenWhenExplicitlyNamePresent() {

		MongoPersistentProperty property = getPropertyFor(DocumentWithExplicitlyRenamedIdPropertyHavingIdAnnotation.class,
				"id");
		assertThat(property.isIdProperty()).isTrue();
	}

	@Test // DATAMONGO-1373
	public void shouldConsiderComposedAnnotationsForIdField() {

		MongoPersistentProperty property = getPropertyFor(DocumentWithComposedAnnotations.class, "myId");
		assertThat(property.isIdProperty()).isTrue();
		assertThat(property.getFieldName()).isEqualTo("_id");
	}

	@Test // DATAMONGO-1373
	public void shouldConsiderComposedAnnotationsForFields() {

		MongoPersistentProperty property = getPropertyFor(DocumentWithComposedAnnotations.class, "myField");
		assertThat(property.getFieldName()).isEqualTo("myField");
	}

	@Test // DATAMONGO-1737
	public void honorsFieldOrderWhenIteratingOverProperties() {

		MongoMappingContext context = new MongoMappingContext();
		BasicMongoPersistentEntity<?> entity = context.getPersistentEntity(Sample.class);

		List<String> properties = new ArrayList<>();

		entity.doWithProperties((MongoPersistentProperty property) -> properties.add(property.getName()));

		assertThat(properties).containsExactly("first", "second", "third");
	}

	@Test // DATAMONGO-1798
	public void fieldTypeShouldReturnActualTypeForNonIdProperties() {

		MongoPersistentProperty property = getPropertyFor(Person.class, "lastname");
		assertThat(property.getFieldType()).isEqualTo(String.class);
	}

	@Test // DATAMONGO-1798
	public void fieldTypeShouldBeObjectIdForPropertiesAnnotatedWithCommonsId() {

		MongoPersistentProperty property = getPropertyFor(Person.class, "id");
		assertThat(property.getFieldType()).isEqualTo(ObjectId.class);
	}

	@Test // DATAMONGO-1798
	public void fieldTypeShouldBeImplicitForPropertiesAnnotatedWithMongoId() {

		MongoPersistentProperty property = getPropertyFor(WithStringMongoId.class, "id");
		assertThat(property.getFieldType()).isEqualTo(String.class);
	}

	@Test // DATAMONGO-1798
	public void fieldTypeShouldBeObjectIdForPropertiesAnnotatedWithMongoIdAndTargetTypeObjectId() {

		MongoPersistentProperty property = getPropertyFor(WithStringMongoIdMappedToObjectId.class, "id");
		assertThat(property.getFieldType()).isEqualTo(ObjectId.class);
	}

	private MongoPersistentProperty getPropertyFor(Field field) {
		return getPropertyFor(entity, field);
	}

	private static <T> MongoPersistentProperty getPropertyFor(Class<T> type, String fieldname) {
		return getPropertyFor(new BasicMongoPersistentEntity<T>(ClassTypeInformation.from(type)), fieldname);
	}

	private static MongoPersistentProperty getPropertyFor(MongoPersistentEntity<?> entity, String fieldname) {
		return getPropertyFor(entity, ReflectionUtils.findField(entity.getType(), fieldname));
	}

	private static MongoPersistentProperty getPropertyFor(MongoPersistentEntity<?> entity, Field field) {
		return new BasicMongoPersistentProperty(Property.of(entity.getTypeInformation(), field), entity,
				SimpleTypeHolder.DEFAULT, PropertyNameFieldNamingStrategy.INSTANCE);
	}

	class Person {

		@Id String id;

		@org.springframework.data.mongodb.core.mapping.Field("foo") String firstname;
		String lastname;

		@org.springframework.data.mongodb.core.mapping.Field(order = -20) String ssn;
	}

	class Sample {

		@org.springframework.data.mongodb.core.mapping.Field(order = 2) String second;
		@org.springframework.data.mongodb.core.mapping.Field(order = 3) String third;
		@org.springframework.data.mongodb.core.mapping.Field(order = 1) String first;
	}

	enum UppercaseFieldNamingStrategy implements FieldNamingStrategy {

		INSTANCE;

		public String getFieldName(PersistentProperty<?> property) {
			return property.getName().toUpperCase(Locale.US);
		}
	}

	enum InvalidFieldNamingStrategy implements FieldNamingStrategy {

		INSTANCE;

		public String getFieldName(PersistentProperty<?> property) {
			return null;
		}
	}

	static class DocumentWithLanguageProperty {

		@Language String lang;
	}

	static class DocumentWithImplicitLanguageProperty {

		String language;
	}

	static class DocumentWithTextScoreProperty {
		@TextScore Float score;
	}

	static class DocumentWithExplicitlyRenamedIdProperty {

		@org.springframework.data.mongodb.core.mapping.Field("id") String id;
	}

	static class DocumentWithExplicitlyRenamedIdPropertyHavingIdAnnotation {

		@Id @org.springframework.data.mongodb.core.mapping.Field("id") String id;
	}

	static class DocumentWithComposedAnnotations {

		@ComposedIdAnnotation @ComposedFieldAnnotation String myId;
		@ComposedFieldAnnotation(name = "myField") String myField;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@org.springframework.data.mongodb.core.mapping.Field
	static @interface ComposedFieldAnnotation {

		@AliasFor(annotation = org.springframework.data.mongodb.core.mapping.Field.class, attribute = "value")
		String name() default "_id";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@Id
	static @interface ComposedIdAnnotation {
	}

	static class WithStringMongoId {

		@MongoId String id;
	}

	static class WithStringMongoIdMappedToObjectId {

		@MongoId(FieldType.OBJECT_ID) String id;
	}
}
