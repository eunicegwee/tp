package seedu.address.model.person;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * A comparator for Person objects that supports multiple sort criteria.
 */
public class PersonComparator implements Comparator<Person> {
    public enum SortField {
        NAME, PHONE, FAVORITE
    }

    public enum SortOrder {
        ASCENDING, DESCENDING
    }

    public static class SortCriteria {
        public final SortField field;
        public final SortOrder order;

        public SortCriteria(SortField field, SortOrder order) {
            this.field = field;
            this.order = order;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof SortCriteria)) {
                return false;
            }
            SortCriteria otherCriteria = (SortCriteria) other;
            return field == otherCriteria.field && order == otherCriteria.order;
        }

        @Override
        public int hashCode() {
            return Objects.hash(field, order);
        }

        @Override
        public String toString() {
            return field + "(" + order + ")";
        }
    }

    private final List<SortCriteria> criteriaList;

    public PersonComparator(List<SortCriteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    @Override
    public int compare(Person p1, Person p2) {
        for (SortCriteria criteria : criteriaList) {
            int result = 0;
            switch (criteria.field) {
            case NAME:
                result = p1.getName().fullName.compareToIgnoreCase(p2.getName().fullName);
                break;
            case PHONE:
                result = p1.getPhone().value.compareTo(p2.getPhone().value);
                break;
            case FAVORITE:
                result = Boolean.compare(p1.isFavourite(), p2.isFavourite());
                break;
            default:
                break;
            }

            if (criteria.order == SortOrder.DESCENDING) {
                result = -result;
            }

            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PersonComparator)) {
            return false;
        }
        PersonComparator otherComparator = (PersonComparator) other;
        return criteriaList.equals(otherComparator.criteriaList);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("criteria", criteriaList).toString();
    }
}

