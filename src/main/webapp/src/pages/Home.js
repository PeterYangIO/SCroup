import React from "react";
import HeaderBar from "../components/common/HeaderBar";
import withStyles from "@material-ui/core/styles/withStyles";
import Paper from "@material-ui/core/Paper/Paper";
import StudyGroups from "../components/home/StudyGroups";
import CreateFab from "../components/home/CreateFab";
import List from "@material-ui/core/List/List";
import ListItem from "@material-ui/core/ListItem/ListItem";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import ListSubheader from "@material-ui/core/ListSubheader/ListSubheader";
import CircularProgress from "@material-ui/core/CircularProgress/CircularProgress";

@withStyles({
    container: {
        display: "flex",
        flexGrow: 1
    },
    courses: {
        flexGrow: 1,
        maxWidth: "400px",
        borderRadius: 0,
        overflowY: "scroll"
    },
    groups: {
        flexGrow: 3,
        padding: "1rem"
    },
    ul: {
        padding: 0
    },
    subheader: {
        background: "#FFF"
    },
    loading: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100%"
    }
})
export default class Home extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            departments: undefined,
            selectedCourse: undefined
        }
    }

    async componentDidMount() {
        /*
        try {
            const response = await NetworkRequest.get("/api/courses");
            if (response.ok) {
                const data = await response.json();
                this.setState({
                    departments: this.groupCourses(data)
                });
            }
        }
        catch (exception) {
            console.error(exception);
        }
        */

        // Dummy data
        const dummyData = [{id: 1, department: "CSCI", number: 102, name: "Fundamentals of Computation"}, {id: 2, department: "CSCI", number: 103, name: "Introduction to Programming"}, {id: 3, department: "CSCI", number: 104, name: "Data Structures and Object Oriented Design"}, {id: 4, department: "CSCI", number: 109, name: "Introduction to Computer Science"}, {id: 5, department: "CSCI", number: 170, name: "Discrete Methods in Computer Science"}, {id: 6, department: "CSCI", number: 201, name: "Principles of Software Development"}, {id: 7, department: "CSCI", number: 270, name: "Introduction to Algorithms and Theory of Computing"}, {id: 8, department: "BUAD", number: 104, name: "Learning About International Commerce"}, {id: 9, department: "BUAD", number: 200, name: "Economic Foundations for Business"}, {id: 10, department: "BUAD", number: 201, name: "Introduction to Business for Non-Majors"}, {id: 11, department: "BUAD", number: 215, name: "Foundations of Business Finance"}];
        setTimeout(() => this.setState({
            departments: this.groupCourses(dummyData)
        }), 1000);
    }

    groupCourses = (courseList) => {
        const groupedCourses = {};
        courseList.forEach(item => {
            if (!groupedCourses.hasOwnProperty(item.department)) {
                groupedCourses[item.department] = [item]
            }
            else {
                groupedCourses[item.department].push(item)
            }
        });
        return groupedCourses;
    };

    render() {
        const {classes} = this.props;
        return (
            <>
                <HeaderBar title="Home"/>
                <main className={classes.container}>
                    <Paper elevation={8} className={classes.courses}>
                        {
                            this.state.departments === undefined
                                ? <div className={classes.loading}><CircularProgress/></div>
                                : <List subheader={<li/>}>
                                    {
                                        Object.keys(this.state.departments).sort().map((department, i) => (
                                            <li key={i}>
                                                <ul className={classes.ul}>
                                                    <ListSubheader
                                                        className={classes.subheader}>{department}</ListSubheader>
                                                    {
                                                        this.state.departments[department].map((course, j) => (
                                                            <ListItem key={j} button>
                                                                <ListItemText
                                                                    primary={`${course.department}-${course.number}`}
                                                                    secondary={course.name}/>
                                                            </ListItem>
                                                        ))
                                                    }
                                                </ul>
                                            </li>
                                        ))
                                    }
                                </List>
                        }
                    </Paper>
                    <section className={classes.groups}>
                        <StudyGroups courseId={this.state.selectedCourse}/>
                    </section>
                </main>

                <CreateFab/>
            </>
        );
    }
}