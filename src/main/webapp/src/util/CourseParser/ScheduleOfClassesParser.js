import cachedCourses from "courses.json";

/**
 * Gets all USC courses using the Schedule of Classes API
 * https://web-app.usc.edu/web/soc/help/README.txt
 *
 * The data has been cached to courses.json and converted to SQL format
 */
class ScheduleOfClassesParser {
    constructor(term) {
        this.term = term;
    }

    async getDepartments() {
        const departmentList = [];
        const response = await fetch(`https://web-app.usc.edu/web/soc/api/departments/${this.term}`);
        const data = await response.json();
        data.department.forEach(item => {
            if (Array.isArray(item.department)) {
                item.department.forEach(department => departmentList.push(department));
            }
            else if (item.hasOwnProperty("department")) {
                departmentList.push(item.department);
            }
        });

        return departmentList;
    }

    async getCourseList(department) {
        const courseList = [];
        const response = await fetch(`https://web-app.usc.edu/web/soc/api/classes/${department}/${this.term}`);
        const data = await response.json();
        console.log(data);

        if (Array.isArray(data.OfferedCourses.course)) {
            data.OfferedCourses.course.forEach(item => {
                const {prefix, number, title} = item.CourseData;
                courseList.push({
                    prefix, number, title
                });
            });
        }
        else {
            const {prefix, number, title} = data.OfferedCourses.course.CourseData;
            courseList.push({
                prefix, number, title
            });
        }

        return courseList;
    }

    async getAllCourses() {
        const allCourses = [];
        const departments = await this.getDepartments();

        for (let department of departments) {
            const courseList = await this.getCourseList(department.code);
            allCourses.push(...courseList);
        }

        return allCourses;
    }

    main() {
        let SQLString = "INSERT INTO courses (department, number, name) VALUES ";
        cachedCourses.forEach(item => {
            SQLString += `('${item.prefix}', ${item.number}, '${item.title}'),`
        });

        return SQLString;
    }
}