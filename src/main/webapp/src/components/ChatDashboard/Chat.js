import React, { Component } from 'react';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import cyan from '@material-ui/core/colors/cyan';
import Icon from '@material-ui/core/Icon';
import Button  from '@material-ui/core/Button';
import Typography  from '@material-ui/core/Typography';
import SendIcon from '@material-ui/icons/Send';
import Card from "@material-ui/core/Card";
import CardActionArea from "@material-ui/core/CardActionArea";
import CardContent from "@material-ui/core/CardContent";
import Divider from "@material-ui/core/Divider";

const styles = theme => ({
    chatBoard: {
        marginTop: 20,
        height: 570,
        overflowY: 'scroll',
        backgroundColor: cyan[50],
    },
    submitButton: {
        marginTop: 27
    },
    card: {
        maxWidth: 345,
        marginTop: 10,
        marginRight: 10,
    },
    card2: {
        maxWidth: 345,
        marginTop: 10,
        marginLeft: 10
    },
    font: {
        fontSize: 10
    },
    leftText: {
        textAlign: 'right'
    },
    height: {
        paddingTop: 10,
        paddingBottom: 10
    }
});

class Chat extends Component {
    constructor(props) {
        super(props);
        this.handleOnChange = this.handleOnChange.bind(this);
        this.handleOnSubmit = this.handleOnSubmit.bind(this);

        this.state = {
            messageText: '',
            messages: [],
            websocket: null,
            user: ""
        }
    }

    componentDidMount() {
        const u = JSON.parse(sessionStorage.getItem("user"));
        this.setState({
            user: u.firstName + " " + u.lastName
        });
        this.connectToWebSocket();
    }


    connectToWebSocket = () => {
        const webSocket = new WebSocket(`ws://${window.location.host}/chat-message/${this.props.groupId}`);
        webSocket.onopen = () => {
            webSocket.send(sessionStorage.getItem("authToken"));
        };

        webSocket.onmessage = async (m) => {
            const data = JSON.parse(m.data);
            if (data.message !== "AUTHENTICATION") {
                this.setState({messages: this.state.messages.concat({
                        "name" : data.username,
                        "message" : data.message
                    })});
            }
        };
        this.setState({
            websocket: webSocket
        });
    };

    handleOnChange(e) {
        this.setState({messageText: e.target.value});
    }

    handleOnSubmit(e) {
        e.preventDefault();
        this.state.websocket.send(this.state.messageText);
        this.setState({
            messageText : ""
        })
    }

    render() {
        const { classes } = this.props;
        console.log(this.state.user);
        return (
            <div>
                <Paper className={classes.chatBoard}>
                    {this.state.messages.map(e => {
                        return (
                            <Grid container>
                                <Grid item xs={2} sm={3}>
                                    {e.name !== this.state.user &&
                                        <Card className={classes.card2}>
                                            <CardActionArea>
                                                <CardContent>
                                                    <Typography component="p">{e.message}</Typography>
                                                </CardContent>
                                                <Divider/>
                                                <CardContent className={classes.height}>
                                                    <Typography component="p" className={classes.font}>
                                                        {e.name}
                                                    </Typography>
                                                </CardContent>
                                            </CardActionArea>
                                        </Card>
                                    }
                                </Grid>
                                <Grid item xs={2} sm={3} />
                                <Grid item xs={2} sm={3} />
                                <Grid item xs={2} sm={3}>
                                    {e.name === this.state.user &&
                                        <Card className={classes.card}>
                                            <CardActionArea>
                                                <CardContent className={classes.leftText}>
                                                    <Typography component="p">{e.message}</Typography>
                                                </CardContent>
                                                <Divider/>
                                                <CardContent className={classes.height}>
                                                    <Typography component="p" className={`${classes.font} ${classes.leftText}`}>
                                                        {e.name}
                                                    </Typography>
                                                </CardContent>
                                            </CardActionArea>
                                        </Card>
                                    }
                                </Grid>
                            </Grid>
                        );
                    })}
                </Paper>
                <div>
                    <form onSubmit={this.handleOnSubmit}>
                        <Grid container>
                            <Grid item sm={11}>
                                <TextField
                                    label="Send Message"
                                    placeholder="Message"
                                    fullWidth
                                    margin="normal"
                                    value = {this.state.messageText}
                                    onChange = {this.handleOnChange}
                                />
                            </Grid>
                            <Grid item sm={1}>
                                <Button type="submit" className={classes.submitButton}>
                                    <SendIcon/>
                                </Button>
                            </Grid>
                        </Grid>
                    </form>
                </div>
            </div>
        );
    }
}


export default withStyles(styles)(Chat);
