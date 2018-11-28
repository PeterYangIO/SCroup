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




const styles = theme => ({
    chatBoard: {
        marginTop: 20,
        height: 570,
        backgroundColor: cyan[50],
    },
    submitButton: {
        marginTop: 27
    },
    messages: {
        width: 600,
        marginBottom: 10,
        marginLeft: 10
    }
});

class Chat extends Component {
    constructor(props) {
        super(props);
        this.handleOnChange = this.handleOnChange.bind(this);
        this.handleOnSubmit = this.handleOnSubmit.bind(this);

        this.state = {
            messageText: '',
            messages: []
        }
    }

    handleOnChange(e) {
        this.setState({messageText: e.target.value});
    }

    handleOnSubmit(e) {
        e.preventDefault();
        this.setState({messages: this.state.messages.concat(this.state.messageText).reverse()})
    }

    render() {
        const { classes } = this.props;
        return (
            <div>
                <Paper className={classes.chatBoard}>
                    {this.state.messages.map((e, i) => {
                        return (
                            <Paper className={classes.messages} key={i}>
                                <Typography variant="h6">
                                    {e}
                                </Typography>
                            </Paper>
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
