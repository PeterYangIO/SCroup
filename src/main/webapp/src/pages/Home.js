import React from "react";
import HeaderBar from "../components/common/HeaderBar";
import withStyles from "@material-ui/core/styles/withStyles";
import Paper from "@material-ui/core/Paper/Paper";
import StudyGroups from "../components/home/StudyGroups";
import CreateFab from "../components/home/CreateFab";
import List from "@material-ui/core/List/List";
import ListItem from "@material-ui/core/ListItem/ListItem";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import CircularProgress from "@material-ui/core/CircularProgress/CircularProgress";
import InputBase from "@material-ui/core/InputBase/InputBase";
import SearchIcon from "@material-ui/icons/Search";
import NetworkRequest from "../util/NetworkRequest";
import {fade} from "@material-ui/core/styles/colorManipulator";
import ExpandLess from "@material-ui/icons/ExpandLess";
import ExpandMore from "@material-ui/icons/ExpandMore";
import Collapse from "@material-ui/core/Collapse/Collapse";

@withStyles(theme => ({
    container: {
        display: "flex",
        flexGrow: 1
    },
    coursesContainer: {
        width: "400px",
        borderRadius: 0,
        display: "flex",
        flexDirection: "column"
    },
    coursesList: {
        height: "100%",
        overflowY: "scroll"
    },
    groupsContainer: {
        flexGrow: 1,
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
    nested: {
        paddingLeft: theme.spacing.unit * 4,
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
            selectedCourse: undefined,
            openedDepartment: -1
        }
    }

    async componentDidMount() {
        await this.getData();
    }

    closeDepartment = () => {
        this.setState({
            openedDepartment: -1
        });
    };

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

    openDepartment = (departmentId) => {
        this.setState({
            openedDepartment: departmentId
        });
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
                                                <div key={i}>
                                                    <ListItem button onClick={
                                                        this.state.openedDepartment === i
                                                            ? () => this.closeDepartment()
                                                            : () => this.openDepartment(i)}>
                                                        <ListItemText primary={department}/>
                                                        {
                                                            this.state.openedDepartment === i
                                                                ? <ExpandLess/>
                                                                : <ExpandMore/>
                                                        }
                                                    </ListItem>
                                                    <Collapse
                                                        key={i}
                                                        in={this.state.openedDepartment === i}
                                                        timeout="auto"
                                                        unmountOnExit>
                                                        {
                                                            this.state.departments[department].map((course, j) => (
                                                                <List component="div" disablePadding onClick={() => this.selectCourse(course)} key={j}>
                                                                    <ListItem button className={classes.nested}>
                                                                        <ListItemText
                                                                            primary={`${course.department}-${course.number}`}
                                                                            secondary={course.name}/>
                                                                    </ListItem>
                                                                </List>
                                                            ))
                                                        }
                                                    </Collapse>
                                                </div>
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