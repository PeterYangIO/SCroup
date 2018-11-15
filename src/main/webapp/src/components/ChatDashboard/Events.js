import React, { Component } from 'react';
import { withStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Button from '@material-ui/core/Button';
import AddIcon from '@material-ui/icons/Add';
import Paper from '@material-ui/core/Paper';
import Modal from '@material-ui/core/Modal';
import TextField from '@material-ui/core/TextField';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

const styles = theme => ({
    addButton: {
        marginTop: 30,
        marginLeft: 10,
        marginBottom: 10
    },
    title: {
        textAlign: 'center',
        marginBottom: 20
    },
    paper: {
        position: 'absolute',
        width: theme.spacing.unit * 50,
        backgroundColor: theme.palette.background.paper,
        boxShadow: theme.shadows[5],
        padding: theme.spacing.unit * 4,
        left: 500,
        top: 100
    },
    textFeld: {
        marginBottom: 10
    },
    dateAndTime: {
        marginTop: 10,
        marginBottom: 10
    }
});

class Events extends Component {
    constructor(props) {
        super(props);
        this.handleOpen = this.handleOpen.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.handleAddEvent = this.handleAddEvent.bind(this);
        this.onChangeTitle = this.onChangeTitle.bind(this);
        this.onChangeLocation = this.onChangeLocation.bind(this);
        this.onChangeDate = this.onChangeDate.bind(this);
        this.onChangeTime = this.onChangeTime.bind(this);
        this.state = {
            modalOpen: false,
            events: [],
            eventTitle: '',
            eventLocation: '',
            date: null,
            time: null
        }
    }

    componentDidMount() {
        this.getEvent();
    }

    getEvent = () => {
        console.log("here");
        this.setState({
            events: []
        });
        fetch("http://localhost:8080/api/event?id=1")
            .then(res => res.json())
            .then(
                (res) => {
                    console.log(res);
                    const events = [];
                    res.forEach(el => {
                        events.push({
                            title: el.title,
                            location: el.location,
                            date: el.date,
                            time: el.time
                        })
                    });
                    this.setState({
                        events: this.state.events.concat(events)
                    })
                }
            )
    };


    handleOpen(e) {
        this.setState({ modalOpen: true });
    }

    handleClose() {
        this.setState({ modalOpen: false });
    }

    handleAddEvent(e) {
        e.preventDefault();
        const item = {
            title: this.state.eventTitle,
            location: this.state.eventLocation,
            date: this.state.date,
            time: this.state.time
        };
        this.addEvent(item);
        this.handleClose();
    }

    addEvent = (event) => {
        return fetch('http://localhost:8080/api/event', {
            method: 'POST',
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(event)
        }).then((res) => {
            this.getEvent();
            console.log(res);
        }).catch((error) => {
            console.log(error);
        });
    };

    onChangeTitle(e) {
        this.setState({eventTitle: e.target.value});
    }

    onChangeLocation(e) {
        this.setState({eventLocation: e.target.value});
    }

    onChangeDate(e) {
        this.setState({date : e.target.value});
    }

    onChangeTime(e) {
        this.setState({time: e.target.value});
    }

    render() {
        const { classes } = this.props;
        return (
            <Paper>
                <div>
                    <Typography variant="h6" className={classes.title}>
                        Upcoming Events
                    </Typography>
                </div>
                <div>
                    {this.state.events.map(e => {
                        return(
                            <ExpansionPanel key={e.title}>
                                <ExpansionPanelSummary expandIcon={<ExpandMoreIcon />}>
                                    <Typography className={classes.heading}>{e.title}</Typography>
                                </ExpansionPanelSummary>
                                <ExpansionPanelDetails>
                                    <Grid container>
                                        <Grid item sm={6}>{e.date + " - " + e.time}</Grid>
                                        <Grid item sm={6}>{e.location}</Grid>
                                    </Grid>
                                </ExpansionPanelDetails>
                            </ExpansionPanel>
                        );
                    })}
                </div>
                <div>
                    <Button variant="fab" color="primary" aria-label="Add" className={classes.addButton} onClick={this.handleOpen}>
                        <AddIcon />
                    </Button>
                </div>
                <Modal
                    aria-labelledby="simple-modal-title"
                    aria-describedby="simple-modal-description"
                    open={this.state.modalOpen}
                    onClose={this.handleClose}
                >
                    <div className={classes.paper}>
                        <Typography variant="h6" className={classes.title}>
                            Add New Event
                        </Typography>
                        <form className={classes.container} noValidate autoComplete="off">
                            <TextField
                                variant="outlined"
                                label="Event"
                                placeholder="Event Title"
                                fullWidth
                                className = {classes.textFeld}
                                value={this.state.title}
                                onChange={this.onChangeTitle}
                            />
                            <TextField
                                variant="outlined"
                                label="Location"
                                placeholder="Enter Location"
                                fullWidth
                                className = {classes.textFeld}
                                value={this.state.eventLocation}
                                onChange={this.onChangeLocation}
                            />
                            <TextField
                                id="date"
                                label="Date"
                                type="date"
                                defaultValue="2017-05-24"
                                value={this.state.date}
                                onChange={this.onChangeDate}
                            />
                            <br />
                            <TextField
                                id="time"
                                label="Time"
                                type="time"
                                defaultValue="07:30"
                                className={classes.dateAndTime}
                                value={this.state.time}
                                onChange={this.onChangeTime}
                            />
                            <br />
                            <Button variant="contained" color="secondary" onClick={this.handleAddEvent}>
                                Add
                            </Button>
                        </form>
                    </div>
                </Modal>
            </Paper>
        );
    }
}


export default withStyles(styles)(Events);
