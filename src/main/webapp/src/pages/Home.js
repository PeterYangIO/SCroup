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
import InputBase from "@material-ui/core/InputBase/InputBase";
import SearchIcon from "@material-ui/icons/Search";
import NetworkRequest from "../util/NetworkRequest";
import {fade} from "@material-ui/core/styles/colorManipulator";

@withStyles(theme => ({
    container: {
        display: "flex",
        flexGrow: 1
    },
    coursesContainer: {
        flexGrow: 1,
        maxWidth: "400px",
        borderRadius: 0,
        display: "flex",
        flexDirection: "column"
    },
    coursesList: {
        height: "100%",
        overflowY: "scroll"
    },
    groupsContainer: {
        flexGrow: 3,
        padding: "2rem",
        overflowY: "scroll"
    },
    loading: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100%"
    },
    searchContainer: {
        backgroundColor: theme.palette.secondary.main,
        padding: theme.spacing.unit
    },
    search: {
        position: "relative",
        borderRadius: theme.shape.borderRadius,
        backgroundColor: fade(theme.palette.common.white, 0.5),
        "&:hover": {
            backgroundColor: fade(theme.palette.common.white, 0.7)
        }
    },
    searchIcon: {
        width: theme.spacing.unit * 8,
        height: "100%",
        position: "absolute",
        pointerEvents: "none",
        display: "flex",
        alignItems: "center",
        justifyContent: "center"
    },
    inputRoot: {
        color: "inherit",
        width: "100%"
    },
    inputInput: {
        paddingTop: theme.spacing.unit,
        paddingRight: theme.spacing.unit,
        paddingBottom: theme.spacing.unit,
        paddingLeft: theme.spacing.unit * 8
    },
    subheader: {
        background: theme.palette.common.white
    },
    ul: {
        background: theme.palette.common.white,
        padding: 0
    }
}))
export default class Home extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            query: "",
            departments: undefined,
            selectedCourse: undefined
        }
    }

    async componentDidMount() {
        // await this.getData(); TODO backend

        // Dummy data
        const dummyData = [{id: 1, department: "CSCI", number: 102, name: "Fundamentals of Computation"}, {id: 2, department: "CSCI", number: 103, name: "Introduction to Programming"}, {id: 3, department: "CSCI", number: 104, name: "Data Structures and Object Oriented Design"}, {id: 4, department: "CSCI", number: 109, name: "Introduction to Computer Science"}, {id: 5, department: "CSCI", number: 170, name: "Discrete Methods in Computer Science"}, {id: 6, department: "CSCI", number: 201, name: "Principles of Software Development"}, {id: 7, department: "CSCI", number: 270, name: "Introduction to Algorithms and Theory of Computing"}, {id: 8, department: "BUAD", number: 104, name: "Learning About International Commerce"}, {id: 9, department: "BUAD", number: 200, name: "Economic Foundations for Business"}, {id: 10, department: "BUAD", number: 201, name: "Introduction to Business for Non-Majors"}, {id: 11, department: "BUAD", number: 215, name: "Foundations of Business Finance"}];
        setTimeout(() => this.setState({
            departments: this._groupCourses(dummyData)
        }), 1000);
    }

    getData = async () => {
        try {
            const response = await NetworkRequest.get("/api/courses", {query: this.state.query});
            if (response.ok) {
                const data = await response.json();
                this.setState({
                    departments: this._groupCourses(data)
                });
            }
            else {
                console.error(response.status, response);
            }
        }
        catch (exception) {
            console.error(exception);
        }
    };

    handleSearch = (event) => {
        this.setState({
            query: event.target.value
        }, this.getData);
    };

    selectCourse = (selectedCourse) => {
        this.setState({selectedCourse})
    };

    _groupCourses = (courseList) => {
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
                    <div className={classes.coursesContainer}>
                        <div className={classes.searchContainer}>
                            <Paper elevation={1} className={classes.search}>
                                <div className={classes.searchIcon}>
                                    <SearchIcon/>
                                </div>
                                <InputBase
                                    placeholder="Search..."
                                    classes={{
                                        root: classes.inputRoot,
                                        input: classes.inputInput
                                    }}
                                    value={this.state.query}
                                    onChange={this.handleSearch}
                                />
                            </Paper>
                        </div>
                        <div className={classes.coursesList}>
                            {
                                this.state.departments === undefined
                                    ? <div className={classes.loading}><CircularProgress/></div>
                                    : <List subheader={<li/>}>
                                        {
                                            Object.keys(this.state.departments).sort().map((department, i) => (
                                                <li key={i}>
                                                    <ul className={classes.ul}>
                                                        <ListSubheader
                                                            className={classes.subheader}>{department}
                                                        </ListSubheader>
                                                        {
                                                            this.state.departments[department].map((course, j) => (
                                                                <ListItem
                                                                    key={j}
                                                                    button
                                                                    selected={this.state.selectedCourse !== undefined && this.state.selectedCourse.id === course.id}
                                                                    onClick={() => this.selectCourse(course)}>
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
                        </div>
                    </div>
                    <section className={classes.groupsContainer}>
                        <StudyGroups course={this.state.selectedCourse} departments={this.state.departments}/>
                    </section>
                </main>

                {
                    sessionStorage.getItem("authToken")
                    && (
                        <CreateFab
                        course={this.state.selectedCourse}
                        departments={this.state.departments}/>
                    )
                }
            </>
        );
    }
}